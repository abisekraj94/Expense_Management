# API Testing Guide (No Authentication)

## Overview
This guide provides step-by-step instructions for testing the Employee Expense Service APIs without JWT authentication.

## Prerequisites
- Application running on `http://localhost:8081`
- PostgreSQL database running
- Redis server running

## API Endpoints

### Employee Endpoints

#### 1. Create Expense
```bash
curl -X POST http://localhost:8081/api/v1/employee/create-expense \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 500.00,
    "description": "Laptop purchase",
    "dateOfExpense": "2024-01-15",
    "documents": ["receipt.pdf", "invoice.pdf"]
  }'
```

#### 2. Update Expense
```bash
curl -X PUT http://localhost:8081/api/v1/employee/update-expense/1?employeeId=1001 \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1001,
    "expenseCategoryId": 1,
    "currency": "USD",
    "amount": 600.00,
    "description": "Updated laptop purchase",
    "dateOfExpense": "2024-01-15",
    "documents": ["receipt.pdf", "invoice.pdf"]
  }'
```

#### 3. Delete Expense
```bash
curl -X DELETE http://localhost:8081/api/v1/employee/delete-expense/1?employeeId=1001
```

### Admin Endpoints

#### 1. Get Employee Expenses
```bash
curl -X GET http://localhost:8081/api/v1/admin/get/1001
```

#### 2. Update Expense Status
```bash
curl -X PUT http://localhost:8081/api/v1/admin/update-expense/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Approved",
    "reviewedBy": 2001
  }'
```

#### 3. Delete Expense (Admin)
```bash
curl -X DELETE http://localhost:8081/api/v1/admin/delete-expense/1
```

## Health Check
```bash
curl http://localhost:8081/actuator/health
```

## Testing Steps

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Verify health endpoint**
   ```bash
   curl http://localhost:8081/actuator/health
   ```

3. **Test expense creation**
   - Use the create expense curl command above
   - Verify response contains expense details with INR conversion

4. **Test admin operations**
   - Use admin endpoints to view and manage expenses
   - No authentication required

5. **Test error scenarios**
   - Try invalid employee IDs
   - Try invalid expense categories
   - Verify proper error responses

## Expected Responses

### Success Response
```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "expenseId": 1,
    "employeeId": 1001,
    "amount": 500.00,
    "convertedAmount": 41500.00,
    "currency": "USD",
    "status": "Requested"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Expense not found",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

## Notes
- All endpoints are now accessible without authentication
- Currency conversion still works with Redis caching
- Database operations remain the same
- Error handling is preserved