# User Management Microservice

A Spring Boot microservice for user management with JWT authentication, Redis caching, and role-based authorization.

## Features

- **JWT Authentication**: Secure token-based authentication
- **Redis Integration**: Token caching with TTL (1 hour)
- **Role-based Authorization**: Employee and Admin roles with different permissions
- **RESTful APIs**: Complete CRUD operations for user management
- **PostgreSQL Database**: Persistent data storage
- **Exception Handling**: Comprehensive error handling with structured responses
- **Input Validation**: Request validation with meaningful error messages
- **Unit Testing**: JUnit and Mockito test coverage

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis**
- **JWT (JSON Web Tokens)**
- **ModelMapper**
- **Maven**
- **Lombok**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

## Setup Instructions

### 1. Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE user_management_db;
```

### 2. Redis Setup

Start Redis server:
```bash
redis-server
```

### 3. Application Configuration

Update `application.properties` with your database and Redis configurations:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/user_management_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT Configuration
jwt.secret=your_secret_key_here
jwt.expiration=3600000
```

### 4. Build and Run

```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "mobileNo": "1234567890",
  "password": "password123",
  "roleId": 2
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

#### Logout
```http
POST /api/auth/logout
Authorization: Bearer <jwt_token>
```

#### Validate Token
```http
GET /api/auth/validate
Authorization: Bearer <jwt_token>
```

### User Management Endpoints

#### Get Current User Profile
```http
GET /api/users/profile
Authorization: Bearer <jwt_token>
```

#### Get User by ID (Admin only)
```http
GET /api/users/{userId}
Authorization: Bearer <admin_jwt_token>
```

#### Get All Users (Admin only)
```http
GET /api/users
Authorization: Bearer <admin_jwt_token>
```

#### Update User
```http
PUT /api/users/{userId}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "firstName": "Updated Name",
  "lastName": "Updated Last Name",
  "mobileNo": "9876543210"
}
```

#### Delete User (Admin only)
```http
DELETE /api/users/{userId}
Authorization: Bearer <admin_jwt_token>
```

### Admin Endpoints

#### Get All Users with Details
```http
GET /api/admin/users
Authorization: Bearer <admin_jwt_token>
```

#### Get User Statistics
```http
GET /api/admin/users/stats
Authorization: Bearer <admin_jwt_token>
```

#### Force Delete User
```http
DELETE /api/admin/users/{userId}/force
Authorization: Bearer <admin_jwt_token>
```

## Default Users

The application comes with default users:

### Admin User
- **Email**: admin@company.com
- **Password**: admin123
- **Role**: ADMIN

### Employee User
- **Email**: employee@company.com
- **Password**: employee123
- **Role**: EMPLOYEE

## Role-based Access Control

### Employee Role
- Can access own profile
- Can update own information
- Can login/logout

### Admin Role
- All Employee permissions
- Can view all users
- Can manage any user
- Can access admin endpoints
- Can view user statistics

## Testing

Run unit tests:
```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/usermanagement/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security configurations
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── application.properties
│       ├── constant-application.properties
│       └── data.sql
└── test/
    └── java/com/usermanagement/
        └── service/        # Unit tests
```

## Inter-service Communication

This microservice is designed to communicate with the `EmployeeSubmissionFinanceApproval` microservice. The shared `expense_category` table enables data consistency across services.

## Error Handling

The application provides structured error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "error": "Detailed error information"
}
```

## Security Features

- **JWT Token Authentication**: Stateless authentication
- **Redis Token Storage**: Secure token management with TTL
- **Password Encryption**: BCrypt password hashing
- **Role-based Authorization**: Method-level security
- **CORS Configuration**: Cross-origin request handling

## Monitoring and Logging

- **SLF4J Logging**: Comprehensive logging at different levels
- **Request/Response Logging**: API call tracking
- **Error Logging**: Exception tracking and debugging

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.