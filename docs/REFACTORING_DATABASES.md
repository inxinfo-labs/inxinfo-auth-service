# Distributed Databases Refactoring

This document describes how to move from a **single database** to **one database per service** (distributed connection).

## Current State

All modules (auth, order, pandit, puja, payment) use **PostgreSQL**. Monolith (app-runner) uses one database (e.g. `inxinfo`). Tables:

- `users`, (auth)
- `orders`, `order_items`, (order)
- `pandits`, `pandit_specializations`, `pandit_bookings`, `pandit_reservations`, (pandit)
- `puja_types`, `puja_bookings`, (puja)
- `password_reset_tokens` (auth)

## Target State: One Database per Service

| Service   | Database   | Tables / entities |
|----------|------------|-------------------|
| Auth     | `auth_db`  | `users`, `password_reset_tokens` |
| Order    | `order_db` | `orders`, `order_items` (and product/item tables as per your schema) |
| Pandit   | `pandit_db`| `pandits`, `pandit_specializations`, `pandit_bookings`, `pandit_reservations` |
| Puja     | `puja_db`  | `puja_types`, `puja_bookings` |

**Rules:**

- Each service connects **only** to its own database.
- Cross-references use **IDs only** (e.g. `user_id`, `pandit_id`). No direct DB access from one service to another’s DB.
- Inter-service communication is via **REST APIs** or events (e.g. Auth exposes `/user/me` or `/users/{id}` for others to call).

## Step 1: Create the Four Databases in PostgreSQL

Run in PostgreSQL (e.g. `psql`):

```sql
CREATE DATABASE auth_db;
CREATE DATABASE order_db;
CREATE DATABASE pandit_db;
CREATE DATABASE puja_db;
CREATE DATABASE payment_db;
```

## Step 2: Migrate Data (If You Already Have Data)

1. **Export** tables from the single DB into SQL files per domain (e.g. `users`, `password_reset_tokens` → `auth_db`; `orders`, `order_items` → `order_db`; etc.) using `pg_dump` or your tool.
2. **Import** each file into the corresponding new database.
3. Ensure foreign keys that pointed to other DBs are replaced by **logical** references (same IDs); no cross-DB FKs.

## Step 3: Point Each Service to Its Own Database

Each module’s `application.yml` already points to PostgreSQL. For **distributed** mode, ensure each service’s URL points to its own database:

- **Auth module / app-runner (monolith):**  
  `jdbc:postgresql://localhost:5432/inxinfo` (one DB for all entities in monolith)

- **Auth service (standalone):**  
  `jdbc:postgresql://localhost:5432/auth_db`

- **Order service:**  
  `jdbc:postgresql://localhost:5432/order_db`

- **Pandit service:**  
  `jdbc:postgresql://localhost:5432/pandit_db`

- **Puja service:**  
  `jdbc:postgresql://localhost:5432/puja_db`

- **Payment service:**  
  `jdbc:postgresql://localhost:5432/payment_db`

When running **app-runner** (monolith):

- **Current behaviour:** App-runner uses a **single** datasource pointing to one PostgreSQL database (e.g. `inxinfo`). All entities (auth, order, pandit, puja, payment) are created there. For monolith mode, ensure that database exists.
- **Option A (recommended for local dev):** Use one DB for monolith; when you run services as **separate** apps, they use their own DBs per module.
- **Option B (multi-datasource in monolith):** Add multiple `DataSource` beans in app-runner and assign each to the correct JPA persistence context.

## Step 4: Remove Cross-DB Dependencies

- Ensure no repository or service in **order**, **pandit**, or **puja** uses `UserRepository` or any auth entity directly. Replace with **HTTP calls** to the auth service (e.g. “get user by id”) when user info is needed.
- Auth service does not access order/pandit/puja tables; it only exposes user and auth APIs.

## Step 5: Run and Test

- Run app-runner (or each service) and verify:
  - Auth: register, login, forgot-password, reset-password, OTP.
  - Order: create order (with `user_id` only), list orders.
  - Pandit: CRUD and bookings (with `user_id` only).
  - Puja: CRUD and bookings (with `user_id` only).

## Quick Reference: Config Snippets (PostgreSQL)

**app-runner (monolith):**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inxinfo
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

**auth-service / order-service / pandit-service / puja-service / payment-service:**  
Use the same pattern with the appropriate database name (`auth_db`, `order_db`, etc.).

After refactoring, each service has its own database and communicates with others only via APIs.
