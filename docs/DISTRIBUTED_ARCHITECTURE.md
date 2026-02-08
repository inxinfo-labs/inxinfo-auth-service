# Distributed Architecture – Single Service per Domain

## Layout (no separate modules)

Each domain is a **single Maven project** that contains both the **code** (entities, repositories, services, controllers) and the **runnable** (main class, port, DB config):

- **auth-service** – Auth code + runnable. Port 8081, `auth_db`. No other inxinfo dependency.
- **puja-service** – Puja code + runnable. Port 8082, `puja_db`. Depends on **auth-service** (AuthClient, user resolution).
- **pandit-service** – Pandit code + runnable. Port 8083, `pandit_db`. Depends on **auth-service**.
- **order-service** – Order code + runnable. Port 8084, `order_db`. Depends on **auth-service** and **puja-service** (OrderItem → PujaType). Saga calls pandit-service over HTTP.
- **api-gateway** – Port 8080, routes to the services above.
- **notification-service** – Port 8085 (optional).
- **app-runner** – Monolith: one JVM, depends on auth-service, puja-service, pandit-service, order-service. Use one DB (e.g. `auth_db`) or separate DBs per domain.

Each service is built with `<classifier>exec</classifier>` so the default JAR is the library (used by app-runner); the runnable is `*-exec.jar` (e.g. `mvn spring-boot:run -pl auth-service` runs the exec jar).

---

## Target Distributed Layout

| Runnable       | Port | Database  | Depends on        | Responsibility              |
|----------------|------|-----------|-------------------|-----------------------------|
| **auth-service**   | 8081 | auth_db   | -                 | Register, login, user, profile, JWT |
| **puja-service**   | 8082 | puja_db   | auth-service      | Puja types, bookings, admin |
| **pandit-service** | 8083 | pandit_db | auth-service      | Pandits, bookings, admin    |
| **order-service**  | 8084 | order_db  | auth-service, puja-service | Orders, items, admin; Saga → pandit HTTP |
| **notification-service** | 8085 | -   | (optional)         | Email / OTP                 |
| **api-gateway**    | 8080 | -         | -                 | Single entry, JWT, route to services |

- **Frontend** uses one base URL: `http://localhost:8080/api` (gateway). Gateway routes to the correct service.
- **Cross-service:** Order/Pandit/Puja store only **userId** (Long). When they need user name/email they call **auth-service** `GET /user/{id}` (internal).

---

## How to Run (Distributed Mode)

1. **Create databases (MySQL):**
   ```sql
   CREATE DATABASE auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE pandit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE puja_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Start services (any order after DBs exist):**
   - Auth:   `mvn spring-boot:run -pl auth-service`
   - Puja:   `mvn spring-boot:run -pl puja-service`
   - Pandit: `mvn spring-boot:run -pl pandit-service`
   - Order:  `mvn spring-boot:run -pl order-service`
   - (Optional) Notification: `mvn spring-boot:run -pl notification-service`
   - Gateway: `mvn spring-boot:run -pl api-gateway`

3. **Frontend:** Set `REACT_APP_API_URL=http://localhost:8080/api` and run the React app (e.g. `npm start`). All API calls go through the gateway.

---

## How to Run (Monolith Mode)

1. Create **auth_db** (and optionally order_db, pandit_db, puja_db if using separate DBs in one JVM).
2. Run: `mvn spring-boot:run -pl app-runner`
3. Backend is at `http://localhost:8080`. Frontend: `REACT_APP_API_URL=http://localhost:8080/api`.

---

## Gateway Routing (Distributed)

| Path prefix   | Forwarded to   |
|---------------|----------------|
| `/auth`, `/user` | auth-service (8081)   |
| `/puja`       | puja-service (8082)   |
| `/pandit`     | pandit-service (8083) |
| `/orders`, `/items`, `/admin/items`, `/admin/products`, `/admin/orders` | order-service (8084) |
| `/admin/pandit` | pandit-service (8083) |
| `/admin/puja` | puja-service (8082)   |
| `/notify`     | notification-service (8085) |

Frontend uses `/api/...`; gateway strips `/api` and forwards by path above.

---

## Auth decoupling (userId + AuthClient)

- **Order**, **PanditBooking**, and **PujaBooking** store only **userId** (Long), not a `User` entity.
- Each of order-service, pandit-service, and puja-service has an **AuthClient** that:
  - In **distributed mode** (when `auth.service.url` is set): calls auth-service `GET /user/{id}` with the request’s `Authorization` header to resolve user display name.
  - In **monolith mode** (no URL): uses **UserRepository** from auth-service (on classpath).
- Auth-service exposes **GET /user/{id}** (authenticated) for other services to fetch user profile (id, name, email, etc.).

**Note:** order-service depends on puja-service for **OrderItem → PujaType** (puja types in orders). When running order-service standalone it needs puja-service on the classpath (as dependency); when running monolith, app-runner pulls in both.
