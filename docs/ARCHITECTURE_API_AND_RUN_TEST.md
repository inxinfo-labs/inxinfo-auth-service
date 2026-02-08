# Puja Store — Architecture, API Documentation & Run/Test

**Single reference document** for architecture, API docs, and run/test instructions. Export this to PDF (e.g. VS Code “Markdown PDF”, pandoc, or print from browser) for one combined PDF.

---

# Part 1 — Architecture

## Distributed Microservices

- **Frontend (React + MUI)** talks **only** to the **API Gateway** at `http://localhost:8080/api`. No direct calls to backend services.
- Each backend service runs on its own port, has its own concerns, and is independently deployable.
- Communication: REST (gateway proxies to services); Order Service uses REST to call Pandit and Notification for the Saga.

## Services & Ports

| Service              | Port | Responsibility |
|----------------------|------|----------------|
| **API Gateway**      | 8080 | Single entry point, CORS, JWT validation, path-based routing |
| **Auth Service**     | 8081 | Login/Registration, OAuth2 (Google), JWT generation, user profile |
| **Puja Service**     | 8082 | Puja catalog and booking (CRUD), `/api/puja/**` |
| **Pandit Service**   | 8083 | Pandit profiles, availability, reserve/release for Saga, `/api/pandit/**` |
| **Order Service**    | 8084 | Order lifecycle, **Saga orchestrator**, `/api/orders/**` |
| **Notification**     | 8085 | SMTP mail; all mails redirected to satish.prasad@inxinfo.com |

## Gateway Routing

| Path             | Forwarded to   | Port |
|------------------|----------------|------|
| /api/auth/**     | Auth Service   | 8081 |
| /api/user/**     | Auth Service   | 8081 |
| /api/puja/**     | Puja Service   | 8082 |
| /api/pandit/**   | Pandit Service | 8083 |
| /api/orders/**   | Order Service  | 8084 |
| /api/notify/**   | Notification   | 8085 |

## Saga (Order Service as Orchestrator)

1. **Create Order** — Order Service creates the order in its DB.
2. **Reserve Pandit** — Order Service calls Pandit Service `POST /pandit/reserve` (orderId, panditId).
3. **Confirm Puja** — Order Service updates order status to CONFIRMED.
4. **Send Notification** — Order Service calls Notification Service `POST /notify/email`.

**Compensation (on any failure):** Order Service calls Pandit Service `POST /pandit/release` and sets order status to CANCELLED.

```
[Frontend] --> [API Gateway :8080] --> [Auth :8081]
                    |                 [Puja :8082]
                    |                 [Pandit :8083]
                    |                 [Order :8084] ---> [Pandit /reserve, /release]
                    |                        |
                    |                        +------------> [Notification :8085]
                    |
                    +--> (JWT validated, X-User-Id forwarded)
```

---

# Part 2 — API Documentation

**Base URL (frontend):** `http://localhost:8080/api`  
**Headers (protected routes):** `Authorization: Bearer <JWT>`, `Content-Type: application/json`

---

## Auth Service (via Gateway)

### POST /api/auth/register
- **Request:** `{ "email", "password", "name", "dob?", "gender?" }`
- **Response 200:** `{ "accessToken": "<JWT>" }`
- **Errors:** 409 Email already registered
- **cURL:** `curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"secret","name":"User"}'`

### POST /api/auth/login
- **Request:** `{ "email", "password" }`
- **Response 200:** `{ "accessToken": "<JWT>" }`
- **Errors:** 401 Invalid credentials
- **cURL:** `curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"secret"}'`

### GET /api/user/me
- **Auth:** Required (JWT)
- **Response 200:** `{ "code": 1002, "message": "...", "data": { "email", "name", "mobileNumber", "dob", "gender", "country", "location", "profilePic", "role" } }`
- **cURL:** `curl -X GET http://localhost:8080/api/user/me -H "Authorization: Bearer <JWT>"`

---

## Puja Service (via Gateway)

### GET /api/puja
- **Response 200:** `{ "code": 2001, "message": "...", "data": [ { "id", "name", "price", "category" } ] }`
- **cURL:** `curl -X GET http://localhost:8080/api/puja`

### GET /api/puja/{id}
- **cURL:** `curl -X GET http://localhost:8080/api/puja/1`

### POST /api/puja/book
- **Auth:** Required. **Request:** pujaTypeId, bookingDate, bookingTime, address, city, pincode, contactPhone, etc.
- **cURL:** `curl -X POST http://localhost:8080/api/puja/book -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"pujaTypeId":1,"bookingDate":"2025-03-01","bookingTime":"10:00","address":"...","city":"...","pincode":"...","contactPhone":"..."}'`

---

## Pandit Service (via Gateway)

### GET /api/pandit/available
- **cURL:** `curl -X GET http://localhost:8080/api/pandit/available`

### GET /api/pandit/{id}
- **cURL:** `curl -X GET http://localhost:8080/api/pandit/1`

### GET /api/pandit/{id}/availability?date=YYYY-MM-DD
- **cURL:** `curl -X GET "http://localhost:8080/api/pandit/1/availability?date=2025-03-01"`

### POST /api/pandit/reserve (Saga – internal)
- **Request:** `{ "orderId", "panditId" }`

### POST /api/pandit/release (Saga – compensation)
- **Request:** `{ "orderId" }`

---

## Order Service (via Gateway)

### POST /api/orders
- **Auth:** Required. **Request:** items (pujaTypeId, quantity), shippingAddress, city, state, pincode, contactPhone, notes, **panditId?** (optional; if present triggers Saga).
- **Response 200:** `{ "code": 3001, "message": "...", "data": { "id", "orderNumber", "status", ... } }`
- **Errors:** 400 Validation, 401 Unauthorized; Saga failure → compensation, order CANCELLED.
- **cURL:** `curl -X POST http://localhost:8080/api/orders -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"items":[{"pujaTypeId":1,"quantity":1}],"shippingAddress":"...","city":"...","pincode":"...","contactPhone":"...","panditId":1}'`

### GET /api/orders
- **Auth:** Required. **cURL:** `curl -X GET http://localhost:8080/api/orders -H "Authorization: Bearer <JWT>"`

### GET /api/orders/{id}
- **Auth:** Required. **cURL:** `curl -X GET http://localhost:8080/api/orders/1 -H "Authorization: Bearer <JWT>"`

### POST /api/orders/{id}/payment/confirm
- **Auth:** Required. **Request:** `{ "paymentId" }`. **cURL:** `curl -X POST http://localhost:8080/api/orders/1/payment/confirm -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"paymentId":"MOCK-1"}'`

---

## Notification Service (via Gateway)

### POST /api/notify/email
- **Request:** `{ "to", "subject", "body" }`. All mails redirected to **satish.prasad@inxinfo.com**.
- **Response 200:** `{ "code": 5001, "message": "Email sent successfully", "data": null }`
- **cURL:** `curl -X POST http://localhost:8080/api/notify/email -H "Content-Type: application/json" -d '{"to":"user@example.com","subject":"Test","body":"Hello"}'`

---

# Part 3 — Run & Test

## Backend (Microservices)

**Prerequisites:** Java 21, Maven 3.6+, MySQL 8 (`authdb` created; credentials in each service `application.yml`).

**Startup order:**

1. Auth: `mvn spring-boot:run -pl auth-module -Dspring-boot.run.profiles=gateway` (8081)
2. Puja: `mvn spring-boot:run -pl puja-service` (8082)
3. Pandit: `mvn spring-boot:run -pl pandit-service` (8083)
4. Order: `mvn spring-boot:run -pl order-service` (8084)
5. Notification: `mvn spring-boot:run -pl notification-service` (8085)
6. Gateway: `mvn spring-boot:run -pl api-gateway` (8080)

**Test Saga:** Get JWT via `POST /api/auth/login`, then `POST /api/orders` with `panditId` in body. Verify order created, pandit reserved, notification sent; on failure, order CANCELLED and pandit released.

**Monolith (optional):** `mvn spring-boot:run -pl app-runner` — single app on 8080, context-path `/api`.

## Frontend (inxinfo-user-portal)

- **.env:** `REACT_APP_API_URL=http://localhost:8080/api`
- **Run:** `npm install && npm start` → http://localhost:3000
- **Testing:** cURL/Postman using base URL above; E2E: login → puja → create order (with/without panditId) → check orders and mail (redirected to satish.prasad@inxinfo.com).

---

*To generate a single PDF: use VS Code extension “Markdown PDF”, or `pandoc ARCHITECTURE_API_AND_RUN_TEST.md -o output.pdf`, or open the HTML version in a browser and use Print → Save as PDF.*
