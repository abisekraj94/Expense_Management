# Finance Admin Service

A comprehensive RESTful microservice for Finance Admin Approval Workflow built with Spring Boot 3 and Java 17.

## Overview

The Finance Admin Service manages expense approval workflows, allowing finance administrators to:
- View pending expenses with pagination
- Approve or reject expenses with reasons
- Generate comprehensive reports with currency conversion
- Send email notifications to employees
- Integrate with Employee Expense Service

## Features

### Core Functionality
- ✅ **Expense Management**: View, approve, and reject employee expenses
- ✅ **Email Notifications**: Professional Thymeleaf templates for approval/rejection
- ✅ **Currency Conversion**: Real-time exchange rates with INR conversion
- ✅ **Comprehensive Reports**: Employee-wise and currency-wise expense analytics
- ✅ **JWT Authentication**: Secure API access with role-based authorization
- ✅ **Pagination Support**: Efficient data handling for large datasets

### Technical Features
- ✅ **Spring Boot 3** with Java 17
- ✅ **PostgreSQL** database with JPA/Hibernate
- ✅ **JWT Security** with Spring Security
- ✅ **ModelMapper** for DTO mapping
- ✅ **Global Exception Handling** with structured error responses
- ✅ **Comprehensive Logging** with SLF4J
- ✅ **Unit Tests** with JUnit 5 and Mockito
- ✅ **Configuration Management** with externalized properties

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.5.3
- **Spring Security**: JWT Authentication
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Email**: Spring Mail with Thymeleaf
- **HTTP Client**: WebClient for currency API
- **Documentation**: OpenAPI/Swagger

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- SMTP server for email notifications

## Setup Instructions

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE finance_admin_db;

-- Create user
CREATE USER finance_admin WITH PASSWORD 'admin123';
GRANT ALL PRIVILEGES ON DATABASE finance_admin_db TO finance_admin;
```

### 2. Environment Configuration

Create `application-local.yml` for local development:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_admin_db
    username: finance_admin
    password: admin123
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

jwt:
  secret: your-secret-key-here
```

### 3. Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd Finance_Admin_Service

# Build the application
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 4. Access the Application

- **Application URL**: http://localhost:8081/api/v1
- **Health Check**: http://localhost:8081/api/v1/actuator/health
- **API Documentation**: http://localhost:8081/api/v1/swagger-ui.html

## API Documentation

### Authentication

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "admin": {
    "adminId": 1,
    "username": "admin",
    "adminName": "Finance Administrator",
    "email": "admin@company.com",
    "role": "FINANCE_ADMIN",
    "isActive": true
  }
}
```

### Expense Management

#### Get Pending Expenses
```http
GET /api/v1/expenses/pending?page=0&size=10
Authorization: Bearer <token>
```

#### Approve Expense
```http
POST /api/v1/expenses/approve
Authorization: Bearer <token>
Content-Type: application/json

{
  "expenseId": 1
}
```

#### Reject Expense
```http
POST /api/v1/expenses/reject
Authorization: Bearer <token>
Content-Type: application/json

{
  "expenseId": 1,
  "rejectionReason": "Invalid receipt provided"
}
```

#### Get Currency Totals
```http
GET /api/v1/expenses/totals/currency
Authorization: Bearer <token>
```

#### Get Total Amount in INR
```http
GET /api/v1/expenses/totals/inr
Authorization: Bearer <token>
```

### Reports

#### Generate Expense Report
```http
GET /api/v1/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5
Authorization: Bearer <token>
```

#### Employee-specific Report
```http
GET /api/v1/reports/expenses/employee/1?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <token>
```

## Configuration

### Application Properties

Key configuration properties in `constant-application.properties`:

```properties
# JWT Configuration
jwt.secret=financeAdminSecretKey2024!@#$%^&*()_+
jwt.expiration=86400000

# Currency API
currency.api.url=https://api.exchangerate-api.com/v4/latest/
currency.base.currency=INR

# Email Templates
email.template.approval=approval-notification
email.template.rejection=rejection-notification

# Pagination
pagination.default.size=10
pagination.max.size=100

# Report Limits
report.max.employees=5
```

### Database Schema

The application uses the following main entities:

- **finance_admins**: Admin user management
- **employees**: Employee information
- **expenses**: Expense records with approval workflow

## Email Templates

Professional HTML email templates are provided for:

1. **Approval Notification** (`approval-notification.html`)
   - Expense details with approval information
   - Professional styling with company branding
   - Clear call-to-action and next steps

2. **Rejection Notification** (`rejection-notification.html`)
   - Expense details with rejection reason
   - Guidance for resubmission
   - Professional styling with clear messaging

## Currency Integration

The service integrates with external currency APIs for real-time exchange rates:

- **API**: Exchange Rate API (https://api.exchangerate-api.com/)
- **Base Currency**: INR
- **Fallback Rates**: Configured for USD, EUR, GBP
- **Conversion**: Automatic conversion to INR for reporting

## Testing

### Unit Tests

Run unit tests with coverage:

```bash
mvn test
mvn jacoco:report
```

### Test Data

Sample data is automatically loaded via `data.sql`:
- 2 Finance Admin users (admin/password)
- 5 Sample employees
- 10 Sample expenses with various statuses

### Default Credentials

```
Username: admin
Password: password

Username: finance.manager  
Password: password
```

## Error Handling

The application provides comprehensive error handling:

- **Global Exception Handler**: Centralized error processing
- **Structured Error Responses**: Consistent error format
- **Validation Errors**: Detailed field-level validation messages
- **HTTP Status Codes**: Proper REST status codes

Example error response:
```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/v1/expenses/approve",
  "validationErrors": [
    {
      "field": "expenseId",
      "message": "Expense ID is required",
      "rejectedValue": null
    }
  ]
}
```

## Logging

Comprehensive logging is implemented:

- **Console Logging**: Development and debugging
- **File Logging**: Production log files in `logs/` directory
- **Log Levels**: Configurable per package
- **Structured Logging**: Consistent log format

## Security

- **JWT Authentication**: Stateless token-based authentication
- **Role-based Authorization**: FINANCE_ADMIN role required
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Configurable cross-origin requests
- **Input Validation**: Comprehensive request validation

## Performance

- **Pagination**: Efficient data loading for large datasets
- **Database Indexing**: Optimized queries with proper indexes
- **Connection Pooling**: HikariCP for database connections
- **Caching**: Configurable caching for frequently accessed data

## Monitoring

- **Health Checks**: Spring Boot Actuator endpoints
- **Metrics**: Application and JVM metrics
- **Logging**: Comprehensive application logging

## Deployment

### Docker Support

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/finance-admin-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables

```bash
export DB_USERNAME=finance_admin
export DB_PASSWORD=admin123
export JWT_SECRET=your-secret-key
export MAIL_USERNAME=your-email@company.com
export MAIL_PASSWORD=your-app-password
```

## Contributing

1. Follow Java coding standards
2. Write comprehensive unit tests
3. Update documentation for new features
4. Use meaningful commit messages
5. Follow the existing code structure

## Support

For support and questions:
- **Email**: finance-team@company.com
- **Documentation**: Internal wiki
- **Issues**: Project issue tracker

## License

© 2024 Company Name. All rights reserved.

---

**Finance Admin Service v1.0.0**  
Built with ❤️ by the Finance Team