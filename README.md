# InxInfo Auth Service

A comprehensive Spring Boot backend service for authentication and authorization with OAuth2 support, built as a multi-module Maven project for a Puja Store application.

## 🏗️ Project Structure

This is a multi-module Maven project with the following modules:

```
inxinfo-auth-service/
├── pom.xml (Parent POM)
├── auth-module/          # Authentication & Authorization module
├── puja-module/          # Puja types and booking module
├── order-module/         # Orders and order items module
└── pandit-module/        # Pandit ji booking module
```

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

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Security** with OAuth2
- **Spring Data JPA**
- **MySQL** database
- **JWT** (JSON Web Tokens)
- **Maven** (Multi-module)
- **Lombok**
- **MapStruct**
- **OpenAPI/Swagger** documentation

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- Google OAuth2 credentials (for OAuth2 login)

## ⚙️ Configuration

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE authdb;
```

2. Update `application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/authdb
    username: your_username
    password: your_password
```

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

1. **Build the project:**
```bash
mvn clean install
```

2. **Run the application:**
```bash
mvn spring-boot:run
```

Or run from your IDE by executing the main class:
- `com.satishlabs.auth.UserLoginRegistrationApplication`

3. **Access the application:**
- API Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

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

### Orders
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get order details
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}/status` - Update order status

### Pandit Booking
- `GET /api/pandit` - Get available pandits
- `GET /api/pandit/{id}` - Get pandit details
- `POST /api/pandit/book` - Book a pandit
- `GET /api/pandit/{id}/availability` - Check pandit availability

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
