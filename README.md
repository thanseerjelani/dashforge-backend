# ⚙️ DashForge - Backend API

> A robust RESTful API built with Spring Boot, Spring Security, and PostgreSQL

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-19-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-000000.svg)](https://jwt.io/)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Database Setup](#-database-setup)
- [Configuration](#-configuration)
- [API Endpoints](#-api-endpoints)
- [Security](#-security)
- [Email Configuration](#-email-configuration)
- [Testing](#-testing)
- [Deployment](#-deployment)

---

## ✨ Features

### 🔐 **Authentication & Authorization**
- JWT-based authentication
- Access & Refresh token mechanism
- Seamless token refresh on email update
- Secure password hashing with BCrypt
- Session management across devices

### 📧 **Password Management**
- OTP-based password reset via email
- Email verification
- Password change with current password verification
- Email notifications for security events

### ✅ **Todo Management**
- Full CRUD operations
- Category and priority management
- Due date tracking
- Overdue detection
- Statistics and analytics

### 📅 **Calendar Events**
- Event creation and management
- Date and time handling
- Category organization
- Upcoming/past event filtering

### 👤 **User Profile**
- Profile updates with seamless token refresh
- Username auto-generation
- Account statistics
- Activity tracking

---

## 🛠️ Tech Stack

### **Core Framework**
- **Spring Boot 3.3.0** - Application framework
- **Java 19** - Programming language
- **Maven** - Dependency management

### **Security**
- **Spring Security** - Authentication & authorization
- **JWT (jjwt 0.12.3)** - Token-based authentication
- **BCrypt** - Password encryption

### **Database**
- **PostgreSQL 16** - Primary database
- **Spring Data JPA** - ORM
- **Hibernate** - JPA implementation
- **HikariCP** - Connection pooling

### **Validation & Mapping**
- **Bean Validation** - Input validation
- **MapStruct** - DTO mapping
- **Lombok** - Boilerplate reduction

### **Email**
- **Spring Mail** - Email functionality
- **SMTP** - Email delivery

### **Development Tools**
- **Spring Boot DevTools** - Hot reload
- **Spring Boot Actuator** - Monitoring
- **Swagger/OpenAPI** - API documentation (optional)

---

## 🏗️ Architecture

### **Project Structure**

```
src/main/java/com/dashboard/api/
├── PersonalDashboardApiApplication.java    # Main application
├── config/
│   ├── SecurityConfig.java                 # Security configuration
│   ├── WebConfig.java                      # CORS configuration
│   └── JwtAuthenticationFilter.java        # JWT filter
├── controller/
│   ├── AuthController.java                 # Authentication endpoints
│   ├── TodoController.java                 # Todo endpoints
│   └── CalendarEventController.java        # Calendar endpoints
├── service/
│   ├── AuthService.java                    # Auth business logic
│   ├── UserService.java                    # User management
│   ├── PasswordService.java                # Password operations
│   ├── TodoService.java                    # Todo logic
│   ├── CalendarEventService.java           # Event logic
│   ├── RefreshTokenService.java            # Token management
│   └── EmailService.java                   # Email operations
├── repository/
│   ├── UserRepository.java                 # User data access
│   ├── RefreshTokenRepository.java         # Token data access
│   ├── OtpRepository.java                  # OTP data access
│   ├── TodoRepository.java                 # Todo data access
│   └── CalendarEventRepository.java        # Event data access
├── entity/
│   ├── User.java                           # User entity
│   ├── RefreshToken.java                   # Refresh token entity
│   ├── Otp.java                            # OTP entity
│   ├── Todo.java                           # Todo entity
│   └── CalendarEvent.java                  # Event entity
├── dto/
│   ├── request/                            # Request DTOs
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── UpdateProfileRequest.java
│   │   ├── ChangePasswordRequest.java
│   │   ├── ForgotPasswordRequest.java
│   │   └── ...
│   └── response/                           # Response DTOs
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── ProfileUpdateResponse.java
│       └── ...
├── mapper/
│   ├── UserMapper.java                     # User DTO mapper
│   ├── TodoMapper.java                     # Todo DTO mapper
│   └── CalendarEventMapper.java            # Event DTO mapper
├── exception/
│   ├── GlobalExceptionHandler.java         # Global error handler
│   ├── UserNotFoundException.java
│   ├── InvalidCredentialsException.java
│   └── ...
└── util/
    ├── JwtUtils.java                       # JWT utilities
    └── UsernameGenerator.java              # Username generation
```

### **Layered Architecture**

```
┌─────────────────────────────────────┐
│          Controller Layer            │  ← REST API endpoints
│     (AuthController, TodoController) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Service Layer               │  ← Business logic
│  (AuthService, TodoService, etc.)    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Repository Layer              │  ← Data access
│   (UserRepository, TodoRepository)   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Database Layer              │  ← PostgreSQL
│         (Tables & Relations)         │
└─────────────────────────────────────┘
```

---

## 🚀 Getting Started

### **Prerequisites**
- Java 19 or higher
- Maven 3.8+
- PostgreSQL 14+
- SMTP server (Gmail, SendGrid, etc.)

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/dashforge.git
   cd dashforge/backend
   ```

2. **Configure database**

   Create PostgreSQL database:
   ```sql
   CREATE DATABASE dashforge_db;
   CREATE USER dashforge_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE dashforge_db TO dashforge_user;
   ```

3. **Configure application**

   Edit `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/dashforge_db
       username: dashforge_user
       password: your_password
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

6. **Verify installation**
   ```
   http://localhost:8080/api/auth/health
   ```

---

## 🗄️ Database Setup

### **Schema Overview**

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE
);

-- OTPs table
CREATE TABLE otps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Todos table
CREATE TABLE todos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    priority VARCHAR(20),
    category VARCHAR(50),
    due_date TIMESTAMP,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Calendar events table
CREATE TABLE calendar_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    category VARCHAR(50),
    priority VARCHAR(20),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### **Indexes**

```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_todos_user_id ON todos(user_id);
CREATE INDEX idx_todos_due_date ON todos(due_date);
CREATE INDEX idx_calendar_events_user_id ON calendar_events(user_id);
CREATE INDEX idx_calendar_events_start_date ON calendar_events(start_date);
```

---

## ⚙️ Configuration

### **application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: DashForge API
  
  # Database Configuration
  datasource:
    url: jdbc:postgresql://localhost:5432/dashforge_db
    username: ${DB_USERNAME:dashforge_user}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: org.postgresql.Driver
  
  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  # Mail Configuration
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:your-email@gmail.com}
    password: ${MAIL_PASSWORD:your-app-password}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# JWT Configuration
app:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key-here-make-it-long-and-random}
    access-token-expiration: 900000      # 15 minutes
    refresh-token-expiration: 604800000  # 7 days
  
  name: DashForge
```

### **Environment Variables**

Create `.env` file (not committed to Git):

```bash
DB_USERNAME=dashforge_user
DB_PASSWORD=your_secure_password
JWT_SECRET=your-super-secret-jwt-key-min-256-bits
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
```

---

## 📡 API Endpoints

### **Base URL**: `http://localhost:8080/api`

### **Authentication Endpoints**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | ❌ |
| POST | `/auth/login` | Login user | ❌ |
| POST | `/auth/refresh` | Refresh access token | ❌ |
| POST | `/auth/logout` | Logout (revoke refresh token) | ✅ |
| POST | `/auth/logout-all` | Logout from all devices | ✅ |
| GET | `/auth/profile` | Get current user profile | ✅ |
| PUT | `/auth/profile` | Update profile (seamless token refresh) | ✅ |

### **Password Management**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/change-password` | Change password | ✅ |
| POST | `/auth/forgot-password` | Send OTP via email | ❌ |
| POST | `/auth/verify-otp` | Verify OTP code | ❌ |
| POST | `/auth/reset-password` | Reset password with OTP | ❌ |

### **Todo Endpoints**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/todos` | Get all todos (with filters) | ✅ |
| GET | `/todos/{id}` | Get todo by ID | ✅ |
| POST | `/todos` | Create new todo | ✅ |
| PUT | `/todos/{id}` | Update todo | ✅ |
| DELETE | `/todos/{id}` | Delete todo | ✅ |
| GET | `/todos/stats` | Get todo statistics | ✅ |

### **Calendar Endpoints**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/calendar/events` | Get all events (with filters) | ✅ |
| GET | `/calendar/events/{id}` | Get event by ID | ✅ |
| POST | `/calendar/events` | Create new event | ✅ |
| PUT | `/calendar/events/{id}` | Update event | ✅ |
| DELETE | `/calendar/events/{id}` | Delete event | ✅ |
| GET | `/calendar/events/stats` | Get event statistics | ✅ |

---

## 🔐 Security

### **JWT Authentication Flow**

```
1. User logs in with credentials
   ↓
2. Server validates credentials
   ↓
3. Server generates:
   - Access Token (15 min expiry)
   - Refresh Token (7 days expiry)
   ↓
4. Client stores both tokens
   ↓
5. Client includes Access Token in requests
   ↓
6. When Access Token expires:
   - Client sends Refresh Token
   - Server validates and issues new Access Token
   ↓
7. When Refresh Token expires:
   - User must login again
```

### **Seamless Email Update**

```java
@Override
public ProfileUpdateResponse updateProfileWithTokenRefresh(UpdateProfileRequest request) {
    User currentUser = getCurrentUser();
    boolean emailChanged = !currentUser.getEmail().equals(request.getEmail());
    
    // Update user details
    currentUser.setEmail(request.getEmail());
    userRepository.save(currentUser);
    
    // If email changed, generate new tokens
    if (emailChanged) {
        // 1. Revoke all old refresh tokens
        refreshTokenService.revokeAllUserTokens(currentUser);
        
        // 2. Generate new access token
        String newAccessToken = jwtUtils.generateAccessToken(currentUser);
        
        // 3. Generate new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(currentUser);
        
        // 4. Return new tokens
        return ProfileUpdateResponse.builder()
                .user(userMapper.toResponse(currentUser))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .emailChanged(true)
                .build();
    }
    
    return ProfileUpdateResponse.builder()
            .user(userMapper.toResponse(currentUser))
            .emailChanged(false)
            .build();
}
```

**Result**: User stays logged in with new credentials! ✨

### **Password Security**

- **BCrypt hashing** with strength 12
- **Minimum requirements**:
    - 8-50 characters
    - At least one uppercase letter
    - At least one lowercase letter
    - At least one digit
- **Password change** requires current password verification
- **Password reset** uses OTP with 10-minute expiry

### **CORS Configuration**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5173",
                    "http://localhost:3011",
                    "https://dashforge.netlify.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## 📧 Email Configuration

### **Gmail Setup**

1. **Enable 2-Factor Authentication** on your Gmail account

2. **Generate App Password**:
    - Go to Google Account Settings
    - Security → 2-Step Verification
    - App passwords → Generate new
    - Copy the 16-character password

3. **Configure in application.yml**:
   ```yaml
   spring:
     mail:
       host: smtp.gmail.com
       port: 587
       username: your-email@gmail.com
       password: your-16-char-app-password
   ```

### **Email Templates**

**OTP Email:**
```
Subject: DashForge - Password Reset OTP

Hello,

You requested to reset your password for your DashForge account.

Your One-Time Password (OTP) is: 123456

This OTP is valid for 10 minutes.

If you did not request this password reset, please ignore this email.

Best regards,
DashForge Team
```

**Password Changed Notification:**
```
Subject: DashForge - Password Changed

Hello,

Your password has been successfully changed for your DashForge account.

If you did not perform this action, please contact support immediately.

Best regards,
DashForge Team
```

---

## 📝 API Request/Response Examples

### **1. Register**

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "username": "johndoe",
      "email": "john@example.com",
      "createdAt": "2024-10-18T10:00:00",
      "updatedAt": "2024-10-18T10:00:00"
    }
  },
  "timestamp": "2024-10-18T10:00:00"
}
```

### **2. Login**

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "Password@123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "username": "johndoe",
      "email": "john@example.com"
    }
  }
}
```

### **3. Update Profile (with Email Change)**

**Request:**
```http
PUT /api/auth/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "John Doe",
  "email": "newemail@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile updated successfully. New authentication tokens provided.",
  "data": {
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "username": "johndoe",
      "email": "newemail@example.com",
      "createdAt": "2024-10-18T10:00:00",
      "updatedAt": "2024-10-18T10:30:00"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "660e8400-e29b-41d4-a716-446655440001",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "emailChanged": true
  },
  "timestamp": "2024-10-18T10:30:00"
}
```

### **4. Create Todo**

**Request:**
```http
POST /api/todos
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README files",
  "priority": "HIGH",
  "category": "WORK",
  "dueDate": "2024-10-25T17:00:00"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Todo created successfully",
  "data": {
    "id": "789e0123-e89b-12d3-a456-426614174000",
    "title": "Complete project documentation",
    "description": "Write comprehensive README files",
    "completed": false,
    "priority": "HIGH",
    "category": "WORK",
    "dueDate": "2024-10-25T17:00:00",
    "createdAt": "2024-10-18T10:00:00",
    "updatedAt": "2024-10-18T10:00:00"
  }
}
```

### **5. Forgot Password (Send OTP)**

**Request:**
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "data": {
    "message": "OTP sent to your email",
    "email": "j***@example.com",
    "expiresIn": 600
  }
}
```

---

## 🧪 Testing

### **Manual Testing with Postman**

Import this collection:

```json
{
  "info": {
    "name": "DashForge API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/auth/register",
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Test User\",\n  \"email\": \"test@example.com\",\n  \"password\": \"Test@12345\",\n  \"confirmPassword\": \"Test@12345\"\n}"
            }
          }
        }
      ]
    }
  ]
}
```

### **Testing Checklist**

Authentication:
- [ ] Register new user
- [ ] Login with valid credentials
- [ ] Login with invalid credentials
- [ ] Refresh access token
- [ ] Logout (single device)
- [ ] Logout all devices

Profile Management:
- [ ] Get user profile
- [ ] Update name only
- [ ] Update email (verify new tokens received)
- [ ] Update with existing email (should fail)

Password Management:
- [ ] Change password with correct current password
- [ ] Change password with wrong current password
- [ ] Send OTP for password reset
- [ ] Verify valid OTP
- [ ] Verify invalid/expired OTP
- [ ] Reset password with valid OTP

Todo Management:
- [ ] Create todo
- [ ] Get all todos
- [ ] Update todo
- [ ] Mark todo as complete
- [ ] Delete todo
- [ ] Get todo statistics

Calendar Management:
- [ ] Create event
- [ ] Get all events
- [ ] Update event
- [ ] Delete event
- [ ] Get event statistics

---

## 🚀 Deployment

### **Production Checklist**

- [ ] Change `ddl-auto` to `validate` or `none`
- [ ] Use strong JWT secret (min 256 bits)
- [ ] Configure production database
- [ ] Set up SSL/TLS
- [ ] Configure CORS for production domain
- [ ] Enable logging (production level)
- [ ] Set up monitoring (Spring Boot Actuator)
- [ ] Configure email service (SendGrid, AWS SES)
- [ ] Set up database backups
- [ ] Configure rate limiting
- [ ] Enable HTTPS only

### **Environment-Specific Configuration**

**application-prod.yml:**
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  mail:
    host: ${MAIL_HOST}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

app:
  jwt:
    secret: ${JWT_SECRET}
```

### **Docker Deployment**

**Dockerfile:**
```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: dashforge_db
      POSTGRES_USER: dashforge_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/dashforge_db
      SPRING_DATASOURCE_USERNAME: dashforge_user
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### **Build & Run**

```bash
# Build
docker-compose up --build

# Check the Docker container is running
docker-compose ps

/* Quick Reference for Future /*

#View logs:
docker logs -f dashforge-backend

# Stop all running containers
docker-compose stop

# Start it again with
docker-compose start

# Or rebuild if needed
docker-compose up -d

# stop and remove containers
docker-compose down

# To remove Containers, networks, images, volumes
docker-compose down --rmi all --volumes

#Rebuild after code changes:
docker-compose up --build
```

---

## 📊 Performance Optimization

### **Database Indexing**

```sql
-- Create indexes for frequently queried columns
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_todos_user_id_due_date ON todos(user_id, due_date);
CREATE INDEX idx_calendar_events_user_id_start_date ON calendar_events(user_id, start_date);
```

### **Connection Pooling**

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### **Caching** (Optional)

```java
@Cacheable(value = "users", key = "#id")
public User getUserById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
}
```

---

## 🔍 Monitoring & Logging

### **Spring Boot Actuator**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**Endpoints:**
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

### **Logging Configuration**

```yaml
logging:
  level:
    root: INFO
    com.dashboard.api: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: logs/dashforge.log
```

---

## 🐛 Troubleshooting

### **Common Issues**

**1. Database Connection Failed**
```
Error: Connection refused
```
**Solution:**
- Check PostgreSQL is running: `sudo service postgresql status`
- Verify database credentials in `application.yml`
- Check firewall settings

**2. JWT Token Invalid**
```
Error: Invalid JWT signature
```
**Solution:**
- Ensure JWT secret is consistent
- Check token expiry time
- Verify token format (Bearer prefix)

**3. Email Sending Failed**
```
Error: Authentication failed
```
**Solution:**
- Use App Password (not regular password)
- Enable "Less secure apps" or use OAuth2
- Check SMTP settings (host, port)

**4. CORS Errors**
```
Error: CORS policy blocked
```
**Solution:**
- Add frontend URL to CORS configuration
- Check `allowedOrigins` in `WebConfig`
- Enable credentials if needed

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Thanseer Jelani**
- GitHub: [@thanseerjelani](https://github.com/thanseerjelani)
- LinkedIn: [Thanseer Jelani](https://www.linkedin.com/in/thanseer-jelani-520768255/)
- Email: imthanseer@gmail.com

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL](https://www.postgresql.org/)
- [JWT](https://jwt.io/)
- [MapStruct](https://mapstruct.org/)

---

## 📞 Support

For support:
- 📧 Email: imthanseer@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/thanseerjelani/dashforge-backend/issues)
- 📖 Documentation: [API Docs (Implementing soon)](https://api.dashforge.com/docs)

---

Made with ❤️ and ☕

**Version**: 1.0.0  
**Last Updated**: October 2024