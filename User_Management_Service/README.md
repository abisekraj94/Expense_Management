# User Management Service

A comprehensive microservice for user management with JWT authentication, built with Spring Boot 3 and Java 17.

## Features

- **User Registration & Authentication**: JWT-based authentication with Redis token storage
- **Role-based Access Control**: Employee and Finance Admin roles
- **Profile Management**: Complete user profile CRUD operations
- **Security**: BCrypt password encryption, JWT tokens with 1-hour TTL
- **Database**: PostgreSQL with proper relationships and audit fields
- **Caching**: Redis integration for JWT token management
- **Testing**: Comprehensive unit tests with JUnit 5 and Mockito
- **Documentation**: Complete API documentation and setup instructions

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database operations)
- **PostgreSQL** (Primary database)
- **Redis** (JWT token storage)
- **ModelMapper** (DTO mapping)
- **Maven** (Build tool)
- **JUnit 5 & Mockito** (Testing)

## Architecture

```
├── Controller Layer (REST endpoints)
├── Service Interface Layer (Business contracts)
├── Service Implementation Layer (Business logic)
├── Repository Layer (Data access)
├── Entity Layer (Database models)
├── DTO Layer (Data transfer objects)
├── Security Layer (JWT & Authentication)
├── Configuration Layer (App configuration)
└── Exception Layer (Error handling)
```

## Database Schema

### Tables

1. **user_role**
   - `role_id` (Primary Key)
   - `role_name` (EMPLOYEE, FINANCE_ADMIN)
   - `role_description`
   - Audit fields (created_by, created_date, updated_by, updated_date)

2. **user_mgnt**
   - `user_id` (Primary Key)
   - `name`, `email`, `password`, `department`
   - `role_id` (Foreign Key to user_role)
   - `is_active` (Boolean)
   - Audit fields (created_by, created_date, updated_by, updated_date)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

## Setup Instructions

### 1. Database Setup

```bash
# Create PostgreSQL database
createdb user_management_db

# Run initialization script
psql -d user_management_db -f src/main/resources/sql/init.sql
```

### 2. Redis Setup

```bash
# Start Redis server (default port 6379)
redis-server

# Verify Redis is running
redis-cli ping
```

### 3. Application Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/user_management_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_redis_password
```

### 4. Build and Run

```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Authentication Endpoints

#### 1. User Registration
```http
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "department": "IT",
  "role": "EMPLOYEE"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "department": "IT",
    "role": "EMPLOYEE",
    "isActive": true,
    "createdDate": "2024-01-15T10:30:00",
    "updatedDate": "2024-01-15T10:30:00"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 2. User Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "userProfile": {
      "userId": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "department": "IT",
      "role": "EMPLOYEE",
      "isActive": true
    }
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 3. User Logout
```http
POST /auth/logout?email=john.doe@example.com
Authorization: Bearer <jwt_token>
```

### User Management Endpoints

#### 1. Get Current User Profile
```http
GET /users/profile
Authorization: Bearer <jwt_token>
```

#### 2. Update User Profile
```http
PUT /users/profile
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.doe@example.com",
  "password": "newpassword123",
  "department": "HR",
  "role": "EMPLOYEE"
}
```

#### 3. Get User Profile by Email (Finance Admin Only)
```http
GET /users/profile/{email}
Authorization: Bearer <jwt_token>
```

#### 4. Health Check
```http
GET /users/health
```

## Sample Users

The initialization script creates sample users for testing:

| Email | Password | Role | Department |
|-------|----------|------|------------|
| john.doe@company.com | password123 | EMPLOYEE | IT |
| jane.smith@company.com | password123 | FINANCE_ADMIN | Finance |
| bob.johnson@company.com | password123 | EMPLOYEE | HR |

## Testing

### Run Unit Tests
```bash
mvn test
```

### Test Coverage
- Service layer: 100% method coverage
- Repository layer: Custom query testing
- Controller layer: Integration testing
- Security layer: JWT validation testing

## Postman Collection

Import the following collection for API testing:

```json
{
  "info": {
    "name": "User Management Service",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Register User",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Test User\",\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\",\n  \"department\": \"IT\",\n  \"role\": \"EMPLOYEE\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/auth/register",
          "host": ["{{baseUrl}}"],
          "path": ["auth", "register"]
        }
      }
    },
    {
      "name": "Login User",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/auth/login",
          "host": ["{{baseUrl}}"],
          "path": ["auth", "login"]
        }
      }
    },
    {
      "name": "Get Profile",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/users/profile",
          "host": ["{{baseUrl}}"],
          "path": ["users", "profile"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api/v1"
    },
    {
      "key": "token",
      "value": ""
    }
  ]
}
```

## Security Features

- **Password Encryption**: BCrypt with salt
- **JWT Tokens**: HS512 algorithm with 1-hour expiration
- **Redis Storage**: Tokens stored in Redis with TTL
- **Role-based Access**: Method-level security annotations
- **CORS Configuration**: Configurable cross-origin requests
- **Input Validation**: Comprehensive validation with custom messages

## Error Handling

The application provides structured error responses:

```json
{
  "status": "ERROR",
  "message": "User not found",
  "timestamp": "2024-01-15T10:30:00"
}
```

Common error scenarios:
- User not found (404)
- Email already exists (409)
- Invalid credentials (401)
- Access denied (403)
- Validation errors (400)

## Monitoring

Health check endpoint available at:
```
GET /api/v1/users/health
```

Actuator endpoints (if enabled):
```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

## Development Standards

- **SOLID Principles**: Applied throughout the codebase
- **Clean Architecture**: Layered architecture with clear separation
- **Exception Handling**: Global exception handler with structured responses
- **Logging**: Comprehensive logging with SLF4J
- **Documentation**: JavaDoc comments for all classes and methods
- **Testing**: Unit tests with high coverage
- **Configuration**: Externalized configuration with profiles

## Contributing

1. Follow the existing code style and architecture
2. Add unit tests for new features
3. Update documentation for API changes
4. Use meaningful commit messages
5. Ensure all tests pass before submitting

## License

This project is licensed under the MIT License.