# Payment System — API, Run & Test

## Overview

- **Backend:** `payment-service` (port 8086) — Razorpay integration, create order, verify signature, store payments/transactions.
- **Gateway:** All payment requests go through API Gateway (port 8080). Frontend **must** call `http://localhost:8080/api` (never 8086 directly).
- **Security:** Razorpay **secret key** is only on the server. Frontend receives only `razorpay_key_id` (public) and `razorpay_order_id` from backend. Signature verification is server-side. Endpoints require JWT (gateway adds `X-User-Id`).

---

## Database

Create schema for payment-service (MySQL):

```sql
CREATE DATABASE paymentdb;
```

Tables `payments` and `transactions` are created by JPA `ddl-auto: update`.

---

## Backend: Razorpay configuration

Set these (env or `payment-service/src/main/resources/application.yml`):

- `RAZORPAY_KEY_ID` — Razorpay API Key (public, used for checkout).
- `RAZORPAY_KEY_SECRET` — Razorpay API Secret (**never** expose to frontend).

Sandbox: [Razorpay Dashboard](https://dashboard.razorpay.com/) → API Keys.

**Where do payments go?** All transactions are collected by the Razorpay account whose API keys you use. To **credit payouts to your UPI** (e.g. `7676134553@upi`): go to [Razorpay Dashboard](https://dashboard.razorpay.com/) → **Account & Settings** → **Bank Account** / **Settlements** and add your UPI ID or bank account. Payouts are then sent there as per your settlement cycle. The app does not set the payout destination; it is configured only in the Razorpay dashboard.

---

## How to run

1. **MySQL:** Ensure `paymentdb` exists.
2. **Auth + Order + Gateway:** Start auth-service (8081), order-service (8084), api-gateway (8080). Other services as per your setup.
3. **Payment service:**
   ```bash
   cd inxinfo-auth-service/payment-service
   set RAZORPAY_KEY_ID=your_key_id
   set RAZORPAY_KEY_SECRET=your_key_secret
   mvn spring-boot:run
   ```
   Or add in `application.yml`:
   ```yaml
   razorpay:
     key_id: your_key_id
     key_secret: your_key_secret
   ```
4. **Frontend:** `REACT_APP_API_URL=http://localhost:8080/api` (gateway). Run from order detail → "Pay with Razorpay" → opens checkout → on success, verify and confirm run automatically.

---

## Troubleshooting: "No static resource payments/create"

This error means the request **did not reach payment-service**. Fix:

1. **Use the API Gateway.** Frontend must call **`http://localhost:8080/api`** (gateway), not `http://localhost:8081` (auth) or any other port. Set `REACT_APP_API_URL=http://localhost:8080/api` in `.env` or when starting the React app.
2. **Start payment-service.** It must run on **port 8086**. From `inxinfo-auth-service/payment-service` run:
   ```bash
   set RAZORPAY_KEY_ID=rzp_test_xxx
   set RAZORPAY_KEY_SECRET=your_secret
   mvn spring-boot:run
   ```
3. **Start api-gateway** on port 8080. The gateway forwards `/api/payments/*` to `http://localhost:8086/payments/*`.
4. If you run a **single** app (e.g. app-runner) that does **not** include the gateway and payment-service, payment routes will not exist; run **api-gateway** and **payment-service** as separate processes.

---

## API (all via Gateway: `http://localhost:8080/api`)

### 1. Create payment order

Creates a Razorpay order and returns public key + order id for checkout.

**Endpoint:** `POST /payments/create`  
**Headers:**
- `Authorization: Bearer <JWT>` (required; gateway validates and adds `X-User-Id`)
- `Content-Type: application/json`
- `Idempotency-Key: <unique-string>` (optional; recommended for duplicate prevention)

**Request body:**
```json
{
  "orderId": "ORD-1234567890",
  "amount": 1500.00,
  "currency": "INR",
  "receipt": "optional-receipt"
}
```

**Response (200):**
```json
{
  "paymentId": 1,
  "orderId": "ORD-1234567890",
  "razorpayOrderId": "order_xxx",
  "razorpayKeyId": "rzp_test_xxx",
  "amount": 1500.00,
  "currency": "INR"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: order-ORD123-unique" \
  -d '{"orderId":"ORD-1234567890","amount":1500,"currency":"INR"}'
```

**Error cases:** 400 (validation), 401 (missing/invalid JWT), 500 (Razorpay/config error).

---

### 2. Verify payment

Verify Razorpay signature and mark payment as SUCCESS. Call after checkout success.

**Endpoint:** `POST /payments/verify`  
**Headers:** `Authorization: Bearer <JWT>`, `Content-Type: application/json`

**Request body:**
```json
{
  "razorpay_order_id": "order_xxx",
  "razorpay_payment_id": "pay_xxx",
  "razorpay_signature": "signature_from_checkout"
}
```

**Response (200):**
```json
{
  "id": 1,
  "orderId": "ORD-1234567890",
  "userId": 1,
  "amount": 1500.00,
  "currency": "INR",
  "status": "SUCCESS",
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "createdAt": "2025-02-06T12:00:00",
  "updatedAt": "2025-02-06T12:01:00"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/payments/verify \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"razorpay_order_id":"order_xxx","razorpay_payment_id":"pay_xxx","razorpay_signature":"sig"}'
```

**Error cases:** 400 (invalid signature / payment not found), 401 (missing JWT).

---

### 3. Get payment by order id

**Endpoint:** `GET /payments/order/{orderId}`  
**Headers:** `Authorization: Bearer <JWT>`

**Response (200):** Same shape as verify response.

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/payments/order/ORD-1234567890" \
  -H "Authorization: Bearer <JWT>"
```

---

## End-to-end flow

1. User opens order detail → clicks **Pay with Razorpay**.
2. Frontend calls `POST /api/payments/create` with `orderId`, `amount` (JWT in header).
3. Backend creates Razorpay order, saves payment (CREATED/INITIATED), returns `razorpayKeyId` and `razorpayOrderId`.
4. Frontend loads Razorpay checkout script and opens checkout with `key` and `order_id`.
5. User pays on Razorpay; checkout calls frontend `handler` with `razorpay_order_id`, `razorpay_payment_id`, `razorpay_signature`.
6. Frontend calls `POST /api/payments/verify` with those three; backend verifies signature and sets payment to SUCCESS.
7. Frontend calls `POST /api/orders/{id}/payment/confirm` with `paymentId`; order-service updates order payment status.
8. Redirect to order detail.

---

## Payment states

- `CREATED` — Payment record created.
- `INITIATED` — Razorpay order created, checkout can be opened.
- `SUCCESS` — Signature verified, payment captured.
- `FAILED` — Verification failed or user cancelled.
- `REFUNDED` — Refunded (future use).

---

## Testing payment locally

Use **Razorpay test keys** from [Dashboard → API Keys](https://dashboard.razorpay.com/app/keys) (test mode). Set `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` in payment-service.

**Ways to pay in test mode:**

| Method   | Test value / note |
|----------|--------------------|
| **Card** | `4111 1111 1111 1111`, any future expiry, any CVV |
| **UPI**  | Use test UPI ID e.g. `success@razorpay` (success) or `failure@razorpay` (failure) |
| **Net Banking** | Select any test bank in Razorpay checkout |

**Flows to test:**

1. **Order (items / puja):**  
   Create order (Products/Puja) → Order list → open order → **Pay with Card / UPI** → complete payment → order shows PAID.

2. **PanditJi booking:**  
   Book a pandit (date, time, address) → after booking you are redirected to **Pay for PanditJi Booking** → **Pay with Card / UPI** → on success booking becomes CONFIRMED. You can also go to **My Pandit Bookings** and click **Pay Now** for any PENDING booking.

**Frontend:** Set `REACT_APP_API_URL=http://localhost:8080/api`. Ensure api-gateway (8080), auth, order, pandit, and payment services are running.

---

## DB schema (reference)

**payments:** id, order_id, user_id, amount, currency, status, razorpay_order_id, razorpay_payment_id, idempotency_key, created_at, updated_at  

**transactions:** id, payment_id, type (CREATE/CAPTURE/REFUND), amount, status, external_id, created_at
