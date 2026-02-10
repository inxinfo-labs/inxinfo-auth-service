# InxInfo Auth Service

A comprehensive Spring Boot backend service for authentication and authorization with OAuth2 support, built as a multi-module Maven project for a Puja Store application. **This is the backend for the [inxinfo-user-portal](https://github.com/inxinfo-labs/inxinfo-user-portal) React frontend** — all API requests from the portal go to this service.

## 🏗️ Project Structure

This is a multi-module Maven project supporting **monolith** (app-runner) and **distributed microservices** (API Gateway + separate services).

```
inxinfo-auth-service/
├── pom.xml (Parent POM)
├── auth-service/           # Auth & User (JWT, OAuth2); code + runnable (8081)
├── puja-service/           # Puja types and booking; code + runnable (8082)
├── pandit-service/         # Pandit profiles, availability, reserve/release; code + runnable (8083)
├── order-service/          # Orders + Saga; code + runnable (8084)
├── api-gateway/            # Single entry (port 8080), CORS, JWT, path-based routing
├── notification-service/   # SMTP mail (port 8085)
├── app-runner/             # Monolith (port 8080, context-path /api) – depends on the 4 services above
└── docs/
    └── API-DOCUMENTATION.md
```

**Frontend** must call **only** the API Gateway at `http://localhost:8080/api` (no direct service calls). See **RUN_AND_TEST.md** for startup order and **docs/API-DOCUMENTATION.md** for full API docs with cURL.

## 🚀 Features

### Authentication Module
- User registration and login
- JWT-based authentication
- OAuth2 integration (Google)
- Password encryption
- User profile management
- Role-based access control (USER, ADMIN)

### Puja Module
- Different types of puja services
- Puja catalog management
- Puja booking functionality
- Puja details and pricing

### Order Module
- Order creation and management
- Order items tracking
- Order status management
- Order history

### Pandit Module
- Pandit ji profiles
- Pandit availability management
- Pandit booking functionality
- Booking calendar management
- **Saga:** `/pandit/reserve`, `/pandit/release` for order-service orchestration

### API Gateway
- Single entry point (port 8080)
- Path-based routing: `/api/auth/**`, `/api/user/**` → auth; `/api/puja/**` → puja; `/api/pandit/**` → pandit; `/api/orders/**` → order; `/api/notify/**` → notification
- CORS and JWT validation; forwards `X-User-Id` to downstream services

### Order Service & Saga
- **Saga orchestration:** Create Order → Reserve Pandit (HTTP) → Confirm Puja → Send Notification (HTTP)
- **Compensation:** On failure, release pandit and set order CANCELLED
- Enable with `order.saga.enabled=true` and `panditId` in create-order request

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Security** with OAuth2
- **Spring Data JPA**
- **PostgreSQL** database
- **JWT** (JSON Web Tokens)
- **Maven** (Multi-module)
- **Lombok**
- **MapStruct**
- **OpenAPI/Swagger** documentation

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL (e.g. 13+)
- Google OAuth2 credentials (for OAuth2 login)

## ⚙️ Configuration

### Database setup (required before first run)

The app uses a PostgreSQL database named **`inxinfo`** (monolith and standalone services). Create it once:

**Option A – psql:**
```bash
psql -U postgres -c "CREATE DATABASE inxinfo;"
```

**Option B – from repo root:**
```bash
psql -U postgres -f scripts/create-db.sql
```

**Option C – in pgAdmin or any client:** run `CREATE DATABASE inxinfo;`

Default connection: `jdbc:postgresql://localhost:5432/inxinfo`, user `postgres`, password `root`. Override with env `DB_PASSWORD` or in `application.yml`.

### OAuth2 Setup

1. Create a Google OAuth2 application at [Google Cloud Console](https://console.cloud.google.com/)
2. Update `application.yml`:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            scope:
              - email
              - profile
```

## 🏃 Running the Application

**Run from the project root** (the folder that contains `auth-service/`, `app-runner/`, and the parent `pom.xml`).  
app-runner depends on auth-service, puja-service, pandit-service, and order-service; they must be **built first**.

1. **Build the project (required once, or after pulling changes):**
```bash
mvn clean install -DskipTests
```

2. **Run the full application (monolith):**
```bash
mvn spring-boot:run -pl app-runner
```

If you see **"Could not find artifact com.satishlabs:auth-service"**, you skipped the build step or ran from the wrong directory. Run `mvn clean install -DskipTests` from the **project root**, then run the command above. See **RUN_MONOLITH.md** for more options (e.g. `-am` to build dependencies in one go).

From your IDE: run the main class **`com.satishlabs.InxinfoApplication`** (in the `app-runner` module) **after** building the root project (Maven → install).

3. **Access the application:**
- API Base URL: `http://localhost:8080/api`
- Swagger UI (monolith): `http://localhost:8080/api/swagger-ui.html` (if enabled). For auth-service alone: `http://localhost:8081/swagger-ui.html`

### Microservices mode (Gateway + 5 services)

See **RUN_AND_TEST.md** for full steps. Summary:

1. Start **auth** (8081), **puja** (8082), **pandit** (8083), **order** (8084), **notification** (8085), then **api-gateway** (8080).
2. Frontend: `REACT_APP_API_URL=http://localhost:8080/api`.

### Integration with inxinfo-user-portal (Frontend)

1. Start this backend (app-runner) on port **8080** as above.
2. In **inxinfo-user-portal**, set `REACT_APP_API_URL=http://localhost:8080/api` in `.env` and run `npm start`.
3. The frontend uses this backend for auth, user profile, puja, pandit, and orders — no API Gateway or other services are required when using this monolith.

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /oauth2/authorization/google` - OAuth2 Google login

### User Management
- `GET /api/user/me` - Get current user profile
- `PUT /api/user/profile` - Update user profile
- `PUT /api/user/password` - Update password
- `POST /api/user/profile-pic` - Upload profile picture
- `GET /api/user/profile-pic` - Get profile picture

### Puja Services
- `GET /api/puja` - Get all puja types
- `GET /api/puja/{id}` - Get puja details
- `POST /api/puja/book` - Book a puja service

### Items (products / ritual items)
- `GET /api/items` - Get active items (public)
- `GET /api/items/{id}` - Get item details (public)

### Orders
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get order details
- `POST /api/orders` - Create new order (body can include `items` [puja] and/or `productItems` [itemId, quantity])
- `PUT /api/orders/{id}/status` - Update order status

### Pandit Booking
- `GET /api/pandit` - Get available pandits
- `GET /api/pandit/{id}` - Get pandit details
- `POST /api/pandit/book` - Book a pandit
- `GET /api/pandit/{id}/availability` - Check pandit availability

### Admin (ADMIN role only)
- **Items:** `GET/POST/PUT/DELETE /api/admin/items` and `/api/admin/items/{id}`
- **Puja:** `GET/POST/PUT/DELETE /api/admin/puja` and `/api/admin/puja/{id}`
- **Pandit:** `GET/POST/PUT/DELETE /api/admin/pandit` and `/api/admin/pandit/{id}`

See **docs/ADMIN_AND_CUSTOMER.md** for default admin login (`admin@inxinfo.com` / `Admin@123`), how to create admin users, and the full customer flow.

## 🔐 Security

- JWT tokens are used for authentication
- Passwords are encrypted using BCrypt
- OAuth2 integration for social login
- CORS configuration for frontend integration
- Role-based access control

## 📦 Module Details

### Auth Module
Handles all authentication and authorization logic, user management, and security configuration.

### Puja Module
Manages puja services catalog, types, pricing, and booking functionality.

### Order Module
Handles order creation, order items management, and order tracking.

### Pandit Module
Manages pandit profiles, availability, and booking functionality.

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📝 License

This project is part of InxInfo Labs.

## 👥 Contributors

- Satish Labs

## 📞 Support

For issues and questions, please contact the development team.
