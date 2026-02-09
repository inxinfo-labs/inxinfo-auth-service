# Admin & Customer Guide

## Admin user

### Default admin account (seeded on first run)

When you run **app-runner** (or auth-module with a database), a default admin user is created if none exists:

- **Email:** `admin@inxinfo.com`
- **Password:** `Admin@123`
- **Role:** `ADMIN`

**Change the password** after first login (e.g. via `PUT /api/user/password` or your profile UI).

### Creating more admin users

1. Register a normal user (e.g. via `POST /api/auth/register`).
2. Admin: call `GET /api/admin/users` to list users, then `PUT /api/admin/users/{id}/role` with body `"ADMIN"` to promote a user to admin.

---

## Admin APIs (require `ADMIN` role)

All admin endpoints require a valid JWT for a user with role **ADMIN**. Send the token in the `Authorization: Bearer <token>` header.  
**Login/register response** now includes `role`, `userId`, and `email` so the frontend can show the admin menu only when `role === "ADMIN"`.

### Items (products / ritual items)

- `GET /api/admin/items` – List all items (including inactive).
- `GET /api/admin/items/{id}` – Get one item.
- `POST /api/admin/items` – Create item (body: `name`, `description`, `price`, `sku`, `active`).
- `PUT /api/admin/items/{id}` – Update item.
- `DELETE /api/admin/items/{id}` – Delete item.

### Puja services

- `GET /api/admin/puja` – List all puja types (including inactive).
- `GET /api/admin/puja/{id}` – Get one puja type.
- `POST /api/admin/puja` – Create puja type (body: `name`, `description`, `price`, `imageUrl`, `durationMinutes`, `category`, `active`).
- `PUT /api/admin/puja/{id}` – Update puja type.
- `DELETE /api/admin/puja/{id}` – Delete puja type.

### Orders (admin)

- `GET /api/admin/orders` – List all orders (all users), with user name, items, total, payment and status.
- `PATCH /api/admin/orders/{id}` – Update order status (body: `{ "orderStatus": "CONFIRMED" }`).
- **Admin notification:** Set `ADMIN_EMAIL` in order-service to receive an email when a new order is placed.

### Users (admin only)

- `GET /api/admin/users` – List all users (for “approve as pandit”, change role, etc.).
- `GET /api/admin/users/{id}` – Get one user.
- `PUT /api/admin/users/{id}/role` – Update role (body: `"USER"` or `"ADMIN"`).
- `PUT /api/admin/users/{id}/enable` – Enable user account.
- `PUT /api/admin/users/{id}/disable` – Disable user account.

### Pandit Ji

- `GET /api/admin/pandit` – List all pandits (including inactive).
- `GET /api/admin/pandit/{id}` – Get one pandit.
- `POST /api/admin/pandit` – Create pandit manually (body: `name`, `email`, `mobileNumber`, `address`, `city`, `state`, `pincode`, `bio`, `experienceYears`, `hourlyRate`, `profileImageUrl`, `specializations`, `status`, `active`).
- **`POST /api/admin/pandit/from-user`** – **Approve an existing user as Pandit.** Body: `{ "userId": <id>, "experienceYears": 5, "hourlyRate": 500, "bio": "...", "address", "city", "state", "pincode", "specializations": [] }`. Name, email, mobile are taken from the user. Send `Authorization: Bearer <admin token>`.
- `PUT /api/admin/pandit/{id}` – Update pandit.
- `DELETE /api/admin/pandit/{id}` – Delete pandit.

---

## Customer flow

1. **Register / Login**  
   - Register: `POST /api/auth/register`.  
   - Login: `POST /api/auth/login` (email + password).  
   - Use the returned JWT for subsequent requests.

2. **Browse catalog (no login required)**  
   - **Puja:** `GET /api/puja`, `GET /api/puja/{id}`.  
   - **Pandit:** `GET /api/pandit`, `GET /api/pandit/available`, `GET /api/pandit/{id}`.  
   - **Items:** `GET /api/items`, `GET /api/items/{id}` (only active items).

3. **Place an order (login required)**  
   - `POST /api/orders` with body:
     - **Puja services:** `items`: `[{ "pujaTypeId": <id>, "quantity": 1 }]`.
     - **Products:** `productItems`: `[{ "itemId": <id>, "quantity": 2 }]`.
     - Plus: `shippingAddress`, `city`, `state`, `pincode`, `contactPhone`, optional `notes`.
   - Order can contain only puja items, only product items, or both. At least one line (puja or product) is required.

4. **View orders**  
   - `GET /api/orders` – My orders.  
   - `GET /api/orders/{id}` or `GET /api/orders/number/{orderNumber}` – Order details (including `orderItems` and `productItems`).

5. **Puja / Pandit booking (optional)**  
   - Book puja: `POST /api/puja/book`.  
   - Book pandit: `POST /api/pandit/book` (and optionally check `GET /api/pandit/{id}/availability?date=...`).

---

## Summary

- **Admin:** Log in with `admin@inxinfo.com` / `Admin@123`. Send `Authorization: Bearer <token>` on every admin request. Use `/api/admin/items` to add products, `/api/admin/users` to list users and change roles, `/api/admin/pandit/from-user` to approve a user as Pandit (after they register as customer).
- **Customer:** Register or log in. Browse **products** via `GET /api/items` (no auth). Place orders via `POST /api/orders` with `productItems: [{ "itemId": <id>, "quantity": 2 }]` (and shipping details).
- **If admin cannot add product:** ensure the request includes header `Authorization: Bearer <accessToken>` and the logged-in user has role `ADMIN` (check login response `role`).
