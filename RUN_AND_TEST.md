# Run & Test Instructions

## Backend (Microservices Mode)

### Prerequisites
- Java 21, Maven 3.6+
- MySQL 8: create database `authdb` and set credentials in each service's `application.yml` (or use same auth-module config).

### Ports

| Service              | Port |
|----------------------|------|
| API Gateway          | 8080 |
| Auth Service         | 8081 |
| Puja Service         | 8082 |
| Pandit Service       | 8083 |
| Order Service        | 8084 |
| Notification Service | 8085 |

### Startup Order

1. **MySQL:** Ensure `authdb` exists and credentials are correct in auth-module and all service runners.

2. **Auth Service**
   ```bash
   mvn spring-boot:run -pl auth-module -Dspring-boot.run.profiles=gateway
   ```
   Runs on **8081** with paths `/auth`, `/user`.

3. **Puja Service**
   ```bash
   mvn spring-boot:run -pl puja-service
   ```
   Runs on **8082**.

4. **Pandit Service**
   ```bash
   mvn spring-boot:run -pl pandit-service
   ```
   Runs on **8083**.

5. **Order Service**
   ```bash
   mvn spring-boot:run -pl order-service
   ```
   Runs on **8084**. Saga is enabled (calls pandit and notification).

6. **Notification Service**
   ```bash
   mvn spring-boot:run -pl notification-service
   ```
   Runs on **8085**. All mails redirected to satish.prasad@inxinfo.com.

7. **API Gateway** (start after at least auth is up)
   ```bash
   mvn spring-boot:run -pl api-gateway
   ```
   Runs on **8080**. Routes `/api/*` to the above services.

### Test Saga Flow

1. Start all six processes (auth, puja, pandit, order, notification, gateway) in the order above.
2. Get a JWT:
   ```bash
   TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"<your-registered-email>","password":"<password>"}' | jq -r '.accessToken')
   ```
3. Create order **with** `panditId` to trigger Saga:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"items":[{"pujaTypeId":1,"quantity":1}],"shippingAddress":"123 St","city":"Bangalore","pincode":"560001","contactPhone":"9999999999","panditId":1}'
   ```
4. Saga steps: Create Order → Reserve Pandit (POST to pandit:8083) → Confirm Puja (update order) → Send Notification (POST to notify:8085). On any failure, compensation: release pandit, set order CANCELLED.

### Monolith Mode (Optional)

Run everything in one JVM (no gateway, no saga HTTP):

```bash
mvn spring-boot:run -pl app-runner
```

App runs on **8080** with context-path **/api**. Frontend base URL: `http://localhost:8080/api`.

---

## Frontend (inxinfo-user-portal)

### Environment Variables

Create `.env`:

```
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_GOOGLE_CLIENT_ID=<optional>
```

For **microservices**, use the gateway URL above. For **monolith**, use `http://localhost:8080/api` (same).

### Build & Run

```bash
cd inxinfo-user-portal
npm install
npm start
```

Open http://localhost:3000. All API calls go to the gateway (or monolith) base URL.

### Testing

- **cURL:** See `docs/API-DOCUMENTATION.md` for per-endpoint cURL examples.
- **Postman:** Import the same endpoints; base URL `http://localhost:8080/api`, add header `Authorization: Bearer <JWT>` for protected routes.
- **E2E:** Login → browse puja → create order (with or without panditId) → check order list and notification (mail redirected to satish.prasad@inxinfo.com).
