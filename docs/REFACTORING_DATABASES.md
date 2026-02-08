# Distributed Databases Refactoring

This document describes how to move from a **single database** (`authdb`) to **one database per service** (distributed connection), as per the refactoring prompt.

## Current State

All modules (auth, order, pandit, puja) currently connect to one MySQL database: `authdb`, with tables:

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

## Step 1: Create the Four Databases in MySQL

Run in MySQL:

```sql
CREATE DATABASE auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE pandit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE puja_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Step 2: Migrate Data (If You Already Have Data in `authdb`)

1. **Export** tables from `authdb` into SQL files per domain (e.g. `users`, `password_reset_tokens` → `auth_db`; `orders`, `order_items` → `order_db`; etc.).
2. **Import** each file into the corresponding new database.
3. Ensure foreign keys that pointed to other DBs are replaced by **logical** references (same IDs); no cross-DB FKs.

## Step 3: Point Each Service to Its Own Database

Update the `spring.datasource.url` in each module’s `application.yml`:

- **Auth module / app-runner (auth context):**  
  `jdbc:mysql://localhost:3306/auth_db`

- **Order service / order-module:**  
  `jdbc:mysql://localhost:3306/order_db`

- **Pandit service / pandit-module:**  
  `jdbc:mysql://localhost:3306/pandit_db`

- **Puja service / puja-module:**  
  `jdbc:mysql://localhost:3306/puja_db`

When running **app-runner** (monolith):

- **Current behaviour:** App-runner uses a **single** datasource pointing to `auth_db`. All entities (auth, order, pandit, puja) are created in `auth_db` when you run the monolith. So for monolith mode, ensure `auth_db` exists and all tables will live there.
- **Option A (recommended for local dev):** Use one DB for monolith: create `auth_db` and run app-runner; all tables go to `auth_db`. When you run **order-service**, **pandit-service**, or **puja-service** as **separate** apps, they use `order_db`, `pandit_db`, `puja_db` respectively.
- **Option B (multi-datasource in monolith):** To have the monolith use four separate DBs, add multiple `DataSource` beans in app-runner and assign each to the correct JPA `EntityManager` / repository package (auth → `auth_db`, order → `order_db`, etc.) via `@Primary` and persistence-unit configuration.

## Step 4: Remove Cross-DB Dependencies

- Ensure no repository or service in **order**, **pandit**, or **puja** uses `UserRepository` or any auth entity directly. Replace with **HTTP calls** to the auth service (e.g. “get user by id”) when user info is needed.
- Auth service does not access order/pandit/puja tables; it only exposes user and auth APIs.

## Step 5: Run and Test

- Run app-runner (or each service) and verify:
  - Auth: register, login, forgot-password, reset-password, OTP.
  - Order: create order (with `user_id` only), list orders.
  - Pandit: CRUD and bookings (with `user_id` only).
  - Puja: CRUD and bookings (with `user_id` only).

## Quick Reference: Config Snippets

**auth-module / app-runner (auth):**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db
    username: root
    password: root
```

**order-service:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/order_db
    username: root
    password: root
```

**pandit-service:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pandit_db
    username: root
    password: root
```

**puja-service:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/puja_db
    username: root
    password: root
```

After refactoring, each service has its own database and communicates with others only via APIs.
