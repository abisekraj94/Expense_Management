# Employee Expense Management System

A comprehensive microservice backend system for employee expense submission and finance admin approval workflow.

## 🚀 Features

- **Employee Expense Submission**: Submit expenses with automatic currency conversion to INR
- **Finance Admin Approval**: Approve/reject expenses with email notifications
- **Currency Conversion**: Real-time currency conversion using external APIs with Redis caching
- **Comprehensive Reporting**: Generate expense reports with filtering and pagination
- **Email Notifications**: Professional email templates for approval/rejection notifications
- **RESTful APIs**: Well-structured REST endpoints with proper error handling
- **Security**: Basic Spring Security configuration
- **Caching**: Redis caching for currency rates with 1-hour TTL
- **Database**: PostgreSQL with proper indexing and relationships

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Security**
- **Spring Mail**
- **PostgreSQL**
- **Redis**
- **Maven**
- **Thymeleaf** (Email templates)
- **ModelMapper**
- **JUnit 5 & Mockito** (Testing)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+
- SMTP server (for email notifications)

## 🔧 Setup & Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd employee-expense-management
```

### 2. Database Setup
```bash
# Create PostgreSQL database
createdb expense_management_db

# Run initialization scripts
psql -d expense_management_db -f sql/init-database.sql
psql -d expense_management_db -f sql/sample-data.sql
```

### 3. Redis Setup
```bash
# Start Redis server
redis-server

# Verify Redis is running
redis-cli ping
```

### 4. Configuration
Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_management_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password


```

### 5. Build & Run
```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```



### Expense Management Endpoints

#### Submit Expense
```http
POST /expenses
Content-Type: application/json

{
  "empId": 1,
  "expenseCategoryId": 1,
  "description": "Business travel to client site",
  "amount": 500.00,
  "currency": "USD",
  "expenseDate": "2024-01-15"
}
```

#### Get Employee Expenses
```http
GET /expenses/employee/{empId}?page=0&size=10&sortBy=createdAt&sortDir=desc
```

#### Get Expense by ID
```http
GET /expenses/{expenseId}
```

#### Get Expenses by Status
```http
GET /expenses/status/{status}?page=0&size=10
```

#### Get Expenses by Date Range
```http
GET /expenses/date-range?startDate=2024-01-01&endDate=2024-01-31&page=0&size=10
```

### Admin Endpoints

#### Get Pending Expenses
```http
GET /admin/expenses/pending?page=0&size=10
```

#### Approve/Reject Expense
```http
PUT /admin/expenses/{expenseId}/approve
Content-Type: application/json

{
  "approverId": 6,
  "action": "APPROVED",
  "rejectionReason": "Optional rejection reason"
}
```

#### Get Currency Totals
```http
GET /admin/expenses/totals/currency
```

### Report Endpoints

#### Employee Expense Report
```http
GET /reports/employee/{empId}?startDate=2024-01-01&endDate=2024-01-31
```

#### Multiple Employee Reports
```http
GET /reports/employees?startDate=2024-01-01&endDate=2024-01-31&page=0&size=5
```

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/com/expense/management/
│   │   ├── config/          # Configuration classes
│   │   ├── constants/       # Application constants
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic services
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── templates/      # Email templates
│       ├── application.properties
│       └── constants-application.properties
└── test/
    └── java/               # Unit tests
```

## 🔍 Key Features Explained

### Currency Conversion
- Integrates with external APIs (exchangerate.host / open.er-api.com)
- Automatic conversion to INR for all expenses
- Redis caching with 1-hour TTL for performance
- Fallback handling for API failures

### Email Notifications
- Professional Thymeleaf templates
- Asynchronous email sending
- Separate templates for approval/rejection
- Comprehensive expense details in emails

### Reporting System
- Employee-wise expense reports
- Currency-wise totals
- Date range filtering
- Pagination support (1-5 employees per page)
- INR conversion totals

### Security & Validation
- Spring Security configuration
- Input validation with custom messages
- Global exception handling
- Structured error responses

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Test Coverage
- Service layer unit tests with Mockito
- Repository integration tests
- Controller endpoint tests
- Exception handling tests

## 📊 Database Schema

### Tables
- `employee` - Employee information
- `expense_category` - Expense categories with limits
- `employee_expense` - Expense submissions
- `employee_expense_docs` - Supporting documents

### Key Relationships
- Employee → Expense (One-to-Many)
- Category → Expense (One-to-Many)
- Employee → Approved Expenses (One-to-Many as approver)

## 🚀 Deployment

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/employee-expense-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
export DB_URL=jdbc:postgresql://localhost:5432/expense_management_db
export DB_USERNAME=postgres
export DB_PASSWORD=password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export MAIL_HOST=smtp.gmail.com
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Email: support@company.com
- Documentation: [API Docs](http://localhost:8080/swagger-ui.html)
- Issues: [GitHub Issues](https://github.com/your-repo/issues)

---

**Note**: This is a microservice designed to integrate with a User Management microservice for complete functionality.