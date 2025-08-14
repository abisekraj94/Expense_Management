# Employee Expense Service - API Testing Guide

## Prerequisites

1. **Application Running**: Ensure the application is running on `http://localhost:8080`
2. **Database Setup**: PostgreSQL database `Exp_Reimburse` is running with proper schema
3. **Redis Setup**: Redis server is running on `localhost:6379`
4. **Test Data**: Database should have expense categories (run `init.sql`)

## Testing Tools Setup

### Option 1: Using Postman

1. **Import Collection**: Import `Employee_Expense_API.postman_collection.json`
2. **Set Variables**:
   - `baseUrl`: `http://localhost:8080`
   - `jwtToken`: (will be set after authentication if implemented)

### Option 2: Using curl Commands

All curl commands are provided below for each endpoint.

## Step-by-Step Testing Workflow

### Step 1: Health Check

**Purpose**: Verify application is running

**Postman**: Use "Health Check" request

**curl**:
```bash
curl -X GET http://localhost:8080/actuator/health
```

**Expected Response**:
```json
{
  "status": "UP"
}
```

---

### Step 2: Employee Operations

#### 2.1 Create Expense (Employee)

**Purpose**: Test expense creation with currency conversion

**Postman**: Use "Create Expense" request

**curl**:
```bash
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 500.00,
    "description": "Laptop purchase for development work",
    "dateOfExpense": "2024-01-15",
    "documents": ["receipt.pdf", "invoice.pdf"]
  }'
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "id": 1,
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 500.00,
    "amountInINR": 41500.00,
    "description": "Laptop purchase for development work",
    "dateOfExpense": "2024-01-15",
    "status": "Requested",
    "documents": ["receipt.pdf", "invoice.pdf"],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Test Variations**:

1. **Different Currencies**:
```bash
# EUR Currency
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1002,
    "expenseCategoryId": 2,
    "currency": "EUR",
    "amount": 300.00,
    "description": "Training certification",
    "dateOfExpense": "2024-01-10",
    "documents": ["certificate.pdf"]
  }'
```

2. **INR Currency (No Conversion)**:
```bash
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1003,
    "expenseCategoryId": 3,
    "currency": "INR",
    "amount": 2000.00,
    "description": "Local travel expenses",
    "dateOfExpense": "2024-01-12"
  }'
```

#### 2.2 Update Expense (Employee)

**Purpose**: Test expense update by employee (only Requested status)

**Postman**: Use "Update Expense" request

**curl**:
```bash
curl -X PUT "http://localhost:8080/api/v1/employee/update-expense/1?employeeId=1001" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 600.00,
    "description": "Updated laptop purchase - higher specification",
    "dateOfExpense": "2024-01-15",
    "documents": ["updated_receipt.pdf", "specification.pdf"]
  }'
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Expense updated successfully",
  "data": {
    "id": 1,
    "employeeId": 1001,
    "amount": 600.00,
    "amountInINR": 49800.00,
    "description": "Updated laptop purchase - higher specification",
    "status": "Requested",
    "updatedAt": "2024-01-15T11:00:00"
  }
}
```

#### 2.3 Delete Expense (Employee)

**Purpose**: Test expense deletion by employee (only Requested status)

**Postman**: Use "Delete Expense" request

**curl**:
```bash
curl -X DELETE "http://localhost:8080/api/v1/employee/delete-expense/3?employeeId=1003"
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Expense deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T11:15:00"
}
```

---

### Step 3: Admin Operations

#### 3.1 Get Expenses by Employee ID

**Purpose**: Test admin viewing employee expenses

**Postman**: Use "Get Expenses by Employee ID" request

**curl**:
```bash
curl -X GET http://localhost:8080/api/v1/admin/get/1001
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Expenses retrieved successfully",
  "data": [
    {
      "id": 1,
      "employeeId": 1001,
      "expenseCategoryId": 1,
      "currency": "USD",
      "amount": 600.00,
      "amountInINR": 49800.00,
      "description": "Updated laptop purchase - higher specification",
      "dateOfExpense": "2024-01-15",
      "status": "Requested",
      "documents": ["updated_receipt.pdf", "specification.pdf"],
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T11:00:00"
    }
  ]
}
```

#### 3.2 Update Expense Status (Admin)

**Purpose**: Test admin updating expense status

**Postman**: Use "Update Expense Status" request

**curl**:
```bash
curl -X PUT http://localhost:8080/api/v1/admin/update-expense/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Approved",
    "reviewedBy": 2001
  }'
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Expense updated successfully",
  "data": {
    "id": 1,
    "status": "Approved",
    "reviewedBy": 2001,
    "updatedAt": "2024-01-15T12:00:00"
  }
}
```

**Test Different Status Updates**:

1. **In Progress**:
```bash
curl -X PUT http://localhost:8080/api/v1/admin/update-expense/2 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Inprogress",
    "reviewedBy": 2001
  }'
```

2. **Rejected**:
```bash
curl -X PUT http://localhost:8080/api/v1/admin/update-expense/2 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Rejected",
    "reviewedBy": 2002
  }'
```

3. **Reimbursed**:
```bash
curl -X PUT http://localhost:8080/api/v1/admin/update-expense/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Reimbursed",
    "reviewedBy": 2001
  }'
```

#### 3.3 Delete Expense (Admin)

**Purpose**: Test admin deleting any expense

**Postman**: Use "Delete Expense (Admin)" request

**curl**:
```bash
curl -X DELETE http://localhost:8080/api/v1/admin/delete-expense/2
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Expense deleted successfully",
  "data": null
}
```

---

## Error Testing Scenarios

### 1. Validation Errors

#### Invalid Currency:
```bash
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "GBP",
    "amount": 500.00,
    "description": "Test expense",
    "dateOfExpense": "2024-01-15"
  }'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "message": "Currency must be USD, EUR, or INR",
  "data": null
}
```

#### Missing Required Fields:
```bash
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "currency": "USD",
    "amount": 500.00
  }'
```

#### Future Date:
```bash
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 500.00,
    "description": "Future expense",
    "dateOfExpense": "2025-12-31"
  }'
```

### 2. Business Logic Errors

#### Update Non-Requested Expense (Employee):
```bash
# First create and approve an expense via admin, then try to update as employee
curl -X PUT "http://localhost:8080/api/v1/employee/update-expense/1?employeeId=1001" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 700.00,
    "description": "Trying to update approved expense",
    "dateOfExpense": "2024-01-15"
  }'
```

#### Non-existent Expense:
```bash
curl -X GET http://localhost:8080/api/v1/admin/get/9999
```

---

## Complete Testing Sequence

### Scenario 1: Full Employee Workflow
```bash
# 1. Create expense
EXPENSE_ID=$(curl -s -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 500.00,
    "description": "Laptop purchase",
    "dateOfExpense": "2024-01-15",
    "documents": ["receipt.pdf"]
  }' | jq -r '.data.id')

# 2. Update expense
curl -X PUT "http://localhost:8080/api/v1/employee/update-expense/${EXPENSE_ID}?employeeId=1001" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 600.00,
    "description": "Updated laptop purchase",
    "dateOfExpense": "2024-01-15"
  }'

# 3. Admin approves
curl -X PUT "http://localhost:8080/api/v1/admin/update-expense/${EXPENSE_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Approved",
    "reviewedBy": 2001
  }'

# 4. Admin reimburses
curl -X PUT "http://localhost:8080/api/v1/admin/update-expense/${EXPENSE_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Reimbursed",
    "reviewedBy": 2001
  }'
```

### Scenario 2: Currency Conversion Testing
```bash
# Test USD to INR
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 100.00,
    "description": "USD conversion test",
    "dateOfExpense": "2024-01-15"
  }'

# Test EUR to INR
curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1002,
    "expenseCategoryId": 2,
    "currency": "EUR",
    "amount": 100.00,
    "description": "EUR conversion test",
    "dateOfExpense": "2024-01-15"
  }'
```

## Performance Testing

### Redis Cache Testing
```bash
# First call - should hit external API
time curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 100.00,
    "description": "Cache test 1",
    "dateOfExpense": "2024-01-15"
  }'

# Second call - should use cached rate (faster)
time curl -X POST http://localhost:8080/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1002,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 200.00,
    "description": "Cache test 2",
    "dateOfExpense": "2024-01-15"
  }'
```

## Monitoring and Logs

### Check Application Logs
```bash
# Windows
type logs\expense-service.log | findstr "ERROR"

# View real-time logs
tail -f logs/expense-service.log
```

### Health and Metrics
```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## Troubleshooting

### Common Issues

1. **Database Connection**: Check PostgreSQL is running and credentials are correct
2. **Redis Connection**: Verify Redis server is running on port 6379
3. **Currency API**: Check internet connection for external currency API calls
4. **Port Conflicts**: Ensure port 8080 is available

### Debug Commands
```bash
# Check if application is running
curl -I http://localhost:8080/actuator/health

# Check database connection
curl http://localhost:8080/actuator/health | jq '.components.db'

# Check Redis connection
curl http://localhost:8080/actuator/health | jq '.components.redis'
```

This comprehensive guide covers all API endpoints with both Postman and curl examples, including error scenarios and performance testing.