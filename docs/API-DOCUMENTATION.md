# API Documentation (Gateway-based)

**Base URL (frontend):** `http://localhost:8080/api`  
All requests from the React app go to the API Gateway. The gateway routes to backend services and validates JWT for protected routes.

**Headers (protected routes):**
```
Authorization: Bearer <JWT>
Content-Type: application/json
```

---

## Auth Service (via Gateway)

### POST /api/auth/register
Register a new user.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "secret",
  "name": "User Name",
  "dob": "1990-01-01",
  "gender": "MALE"
}
```

**Response:** `200`  
```json
{ "accessToken": "<JWT>" }
```

**Errors:** `409` Email already registered.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret","name":"User"}'
```

---

### POST /api/auth/login
Login with email/password.

**Request:**
```json
{ "email": "user@example.com", "password": "secret" }
```

**Response:** `200`  
```json
{ "accessToken": "<JWT>" }
```

**Errors:** `401` Invalid credentials.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret"}'
```

---

### GET /api/user/me
Get current user profile (requires JWT).

**Response:** `200`  
```json
{
  "code": 1002,
  "message": "Profile fetched successfully",
  "data": {
    "email": "user@example.com",
    "name": "User",
    "mobileNumber": null,
    "dob": "1990-01-01",
    "gender": "MALE",
    "country": null,
    "location": null,
    "profilePic": null,
    "role": "USER"
  }
}
```

**Errors:** `401` Missing or invalid token.

**cURL:**
```bash
curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer <JWT>"
```

---

## Puja Service (via Gateway)

### GET /api/puja
List all puja types (public).

**Response:** `200`  
```json
{
  "code": 2001,
  "message": "Puja types fetched successfully",
  "data": [ { "id": 1, "name": "...", "price": 1000, "category": "..." } ]
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/puja
```

---

### GET /api/puja/{id}
Get puja type by ID (public).

**cURL:**
```bash
curl -X GET http://localhost:8080/api/puja/1
```

---

### POST /api/puja/book
Book a puja (requires JWT).

**Request:**
```json
{
  "pujaTypeId": 1,
  "bookingDate": "2025-03-01",
  "bookingTime": "10:00",
  "specialInstructions": "...",
  "address": "...",
  "city": "...",
  "pincode": "...",
  "contactPhone": "..."
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/puja/book \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"pujaTypeId":1,"bookingDate":"2025-03-01","bookingTime":"10:00","address":"...","city":"...","pincode":"...","contactPhone":"..."}'
```

---

## Pandit Service (via Gateway)

### GET /api/pandit/available
List available pandits (public).

**cURL:**
```bash
curl -X GET http://localhost:8080/api/pandit/available
```

---

### GET /api/pandit/{id}
Get pandit by ID (public).

**cURL:**
```bash
curl -X GET http://localhost:8080/api/pandit/1
```

---

### GET /api/pandit/{id}/availability?date=2025-03-01
Check availability (public).

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/pandit/1/availability?date=2025-03-01"
```

---

### POST /api/pandit/reserve (Saga – internal)
Reserve pandit for order (called by order-service).

**Request:**
```json
{ "orderId": 1, "panditId": 1 }
```

---

### POST /api/pandit/release (Saga – compensation)
Release reservation (called by order-service).

**Request:**
```json
{ "orderId": 1 }
```

---

## Order Service (via Gateway)

### POST /api/orders
Create order (requires JWT). Optional `panditId` triggers Saga (reserve pandit → confirm → notify).

**Request:**
```json
{
  "items": [ { "pujaTypeId": 1, "quantity": 2 } ],
  "shippingAddress": "...",
  "city": "...",
  "state": "...",
  "pincode": "...",
  "contactPhone": "...",
  "notes": "...",
  "panditId": 1
}
```

**Response:** `200`  
```json
{
  "code": 3001,
  "message": "Order created successfully",
  "data": { "id": 1, "orderNumber": "ORD-...", "status": "CONFIRMED", ... }
}
```

**Errors:** `400` Validation; `401` Unauthorized; Saga failure returns error and compensates (release pandit, order CANCELLED).

**cURL:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"items":[{"pujaTypeId":1,"quantity":1}],"shippingAddress":"...","city":"...","pincode":"...","contactPhone":"..."}'
```

---

### GET /api/orders
List user orders (requires JWT).

**cURL:**
```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer <JWT>"
```

---

### GET /api/orders/{id}
Get order by ID (requires JWT).

**cURL:**
```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer <JWT>"
```

---

### POST /api/orders/{id}/payment/confirm
Confirm payment (requires JWT).

**Request:** `{ "paymentId": "MOCK-1" }`

**cURL:**
```bash
curl -X POST http://localhost:8080/api/orders/1/payment/confirm \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"paymentId":"MOCK-1"}'
```

---

## Notification Service (via Gateway)

### POST /api/notify/email
Send email. All mails are redirected to **satish.prasad@inxinfo.com**.

**Request:**
```json
{
  "to": "customer@example.com",
  "subject": "Order Confirmed",
  "body": "Your order has been confirmed."
}
```

**Response:** `200`  
```json
{ "code": 5001, "message": "Email sent successfully", "data": null }
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/notify/email \
  -H "Content-Type: application/json" \
  -d '{"to":"user@example.com","subject":"Test","body":"Hello"}'
```

---

## Gateway Routes Summary

| Path           | Service            | Port |
|----------------|--------------------|------|
| /api/auth/**   | auth-service       | 8081 |
| /api/user/**   | auth-service       | 8081 |
| /api/puja/**   | puja-service       | 8082 |
| /api/pandit/** | pandit-service     | 8083 |
| /api/orders/** | order-service      | 8084 |
| /api/notify/** | notification-service | 8085 |

Frontend must use **only** `http://localhost:8080/api` as base URL (no direct service calls).
