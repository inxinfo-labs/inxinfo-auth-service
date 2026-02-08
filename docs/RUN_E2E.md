# End-to-End Run (Distributed Mode)

Use this to run the full stack so the **frontend** (React) works against the **distributed backend** (API Gateway + auth, puja, pandit, order, optional notification).

## Prerequisites

- Java 21, Maven, Node.js, MySQL
- MySQL running with user `root` / password `root` (or adjust `application.yml` in each service)

## 1. Create databases

In MySQL:

```sql
CREATE DATABASE auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE pandit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE puja_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. Start backend services

From the **inxinfo-auth-service** (backend) root:

```bash
# Terminal 1 – Auth (8081)
mvn spring-boot:run -pl auth-service

# Terminal 2 – Puja (8082)
mvn spring-boot:run -pl puja-service

# Terminal 3 – Pandit (8083)
mvn spring-boot:run -pl pandit-service

# Terminal 4 – Order (8084)
mvn spring-boot:run -pl order-service

# Terminal 5 (optional) – Notification (8085)
mvn spring-boot:run -pl notification-service

# Terminal 6 – API Gateway (8080) – start last so it can reach the others
mvn spring-boot:run -pl api-gateway
```

Wait until each logs "Started ..." before starting the next. Gateway should be started last.

## 3. Start frontend

From the **inxinfo-user-portal** (frontend) root:

```bash
# Use gateway as API base (default in .env.example)
set REACT_APP_API_URL=http://localhost:8080/api
npm start
```

Or create a `.env` with:

```
REACT_APP_API_URL=http://localhost:8080/api
```

Then run `npm start`. The app will open at `http://localhost:3000` and all API calls will go through the gateway.

## 4. Verify

- Register / login via the app (hits auth-service via gateway).
- Browse puja/pandit, place orders, view profile (all go through gateway to the right service).
- For **monolith** instead: create only `auth_db`, run `mvn spring-boot:run -pl app-runner`, and use the same frontend URL (`http://localhost:8080/api`).

See **DISTRIBUTED_ARCHITECTURE.md** for module vs service layout and gateway routing.
