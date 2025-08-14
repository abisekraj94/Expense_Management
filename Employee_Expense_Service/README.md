# Employee Expense Service

A Spring Boot microservice for managing employee expense submissions with currency conversion, Redis caching, and JWT authentication.

## Features

- **Expense Management**: Create, update, delete, and view expense submissions
- **Currency Conversion**: Automatic conversion to INR using external API with Redis caching
- **Role-based Access**: Separate endpoints for employees and admins
- **Redis Caching**: Currency rates cached for 1 hour to improve performance
- **PostgreSQL Database**: Robust data persistence with proper relationships
- **Comprehensive Testing**: Unit tests with JUnit and Mockito

## Technology Stack

- Java 17
- Spring Boot 3.5.3

- Spring Data JPA
- PostgreSQL
- Redis
- Maven
- Lombok
- ModelMapper
- JUnit 5 & Mockito

## Database Schema

### Tables

1. **expense_category**: Categories with spending limits
2. **employee_expense**: Main expense records with currency conversion
3. **employee_expense_docs**: Document attachments for expenses

## API Endpoints

### Employee Endpoints (`/api/v1/employee`)

- `POST /create-expense` - Create new expense
- `PUT /update-expense/{id}` - Update expense (Requested status only)
- `DELETE /delete-expense/{id}` - Delete expense (Requested status only)

### Admin Endpoints (`/api/v1/admin`)

- `GET /get/{employeeId}` - Get expenses by employee ID
- `PUT /update-expense/{id}` - Update expense status
- `DELETE /delete-expense/{id}` - Delete any expense

## Setup Instructions

### Prerequisites

- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

### Database Setup

1. Install PostgreSQL and create database:
```sql
CREATE DATABASE expense_db;
CREATE USER expense_user WITH PASSWORD 'expense_pass';
GRANT ALL PRIVILEGES ON DATABASE expense_db TO expense_user;
```

2. Run the initialization script:
```bash
psql -U expense_user -d expense_db -f init.sql
```

### Redis Setup

1. Install and start Redis server:
```bash
# Windows (using Chocolatey)
choco install redis-64
redis-server

# Linux/Mac
sudo apt-get install redis-server
redis-server
```

### Application Setup

1. Clone the repository
2. Update `application.yml` with your database and Redis configurations
3. Build the application:
```bash
mvn clean compile
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/expense_db
    username: expense_user
    password: expense_pass
  
  redis:
    host: localhost
    port: 6379



currency:
  api:
    url: https://open.er-api.com/v6/latest
```

## Currency Conversion

The service integrates with external currency APIs:
- Primary: `https://open.er-api.com/v6/latest`
- Alternative: `https://exchangerate.host`

Currency rates are cached in Redis with 1-hour TTL for optimal performance.

## Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## API Documentation

### Create Expense Request
```json
{
  "employeeId": 1001,
  "expenseCategoryId": 1,
  "currency": "USD",
  "amount": 500.00,
  "description": "Laptop purchase",
  "dateOfExpense": "2024-01-15",
  "documents": ["receipt.pdf", "invoice.pdf"]
}
```

### Update Expense Status (Admin)
```json
{
  "status": "Approved",
  "reviewedBy": 2001
}
```

## Error Handling

The application provides structured error responses:
```json
{
  "success": false,
  "message": "Expense not found",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

## Security

- Input validation
- SQL injection prevention
- Secure data handling

## Monitoring

Health check endpoint: `GET /actuator/health`

## Development Standards

- SOLID principles
- Clean code practices
- Comprehensive exception handling
- Meaningful logging
- Configuration-driven setup
- Proper package structure
- Unit test coverage

## Contributing

1. Follow the existing code style
2. Add unit tests for new features
3. Update documentation
4. Use meaningful commit messages

## License

This project is licensed under the MIT License.