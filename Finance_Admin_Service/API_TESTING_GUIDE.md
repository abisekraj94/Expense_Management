# Finance Admin Service - API Testing Guide

## Prerequisites

1. **Application Running**: Ensure the service is running on `http://localhost:8082/api/v1`
2. **Database**: PostgreSQL with sample data loaded
3. **Testing Tool**: Postman, curl, or any REST client

## Step-by-Step API Testing

### Phase 1: Health Check & Setup

#### 1.1 Verify Application Health
```http
GET http://localhost:8082/api/v1/actuator/health
```
**Expected Response**: `200 OK` with health status

#### 1.2 Check Application Info
```http
GET http://localhost:8082/api/v1/actuator/info
```

### Phase 2: Authentication Testing

#### 2.1 Admin Login (Primary)
```http
POST http://localhost:8082/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```
**Expected Response**:
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
**Action**: Save the `accessToken` for subsequent requests

#### 2.2 Alternative Admin Login
```http
POST http://localhost:8082/api/v1/auth/login
Content-Type: application/json

{
  "username": "finance.manager",
  "password": "password"
}
```

#### 2.3 Invalid Login Test
```http
POST http://localhost:8082/api/v1/auth/login
Content-Type: application/json

{
  "username": "invalid",
  "password": "wrong"
}
```
**Expected Response**: `401 Unauthorized`

### Phase 3: Expense Management Testing

**Note**: Include `Authorization: Bearer <token>` header for all requests below

#### 3.1 Get Pending Expenses
```http
GET http://localhost:8082/api/v1/expenses/pending?page=0&size=10
Authorization: Bearer <your-jwt-token>
```
**Expected Response**: Paginated list of pending expenses

#### 3.2 Get Specific Expense
```http
GET http://localhost:8082/api/v1/expenses/1
Authorization: Bearer <your-jwt-token>
```

#### 3.3 Approve Expense
```http
POST http://localhost:8082/api/v1/expenses/approve
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "expenseId": 1
}
```
**Expected Response**: Updated expense with `APPROVED` status

#### 3.4 Reject Expense
```http
POST http://localhost:8082/api/v1/expenses/reject
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "expenseId": 3,
  "rejectionReason": "Receipt is not clear and expense category is not justified"
}
```
**Expected Response**: Updated expense with `REJECTED` status

#### 3.5 Get Currency Totals
```http
GET http://localhost:8082/api/v1/expenses/totals/currency
Authorization: Bearer <your-jwt-token>
```
**Expected Response**: Array of currency totals

#### 3.6 Get Total Amount in INR
```http
GET http://localhost:8082/api/v1/expenses/totals/inr
Authorization: Bearer <your-jwt-token>
```
**Expected Response**: Total amount as BigDecimal

#### 3.7 Sync Expense from Employee Service
```http
POST http://localhost:8082/api/v1/expenses/1/sync
Authorization: Bearer <your-jwt-token>
```

### Phase 4: Reports Testing

#### 4.1 Generate Multi-Employee Report
```http
GET http://localhost:8082/api/v1/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5
Authorization: Bearer <your-jwt-token>
```

#### 4.2 Employee-Specific Report
```http
GET http://localhost:8082/api/v1/reports/expenses/employee/1?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <your-jwt-token>
```

#### 4.3 Date Range Only Report
```http
GET http://localhost:8082/api/v1/reports/expenses?startDate=2024-01-01&endDate=2024-01-31&page=0&size=5
Authorization: Bearer <your-jwt-token>
```

#### 4.4 Default Date Range Report
```http
GET http://localhost:8082/api/v1/reports/expenses?page=0&size=5
Authorization: Bearer <your-jwt-token>
```

### Phase 5: Error Handling Testing

#### 5.1 Unauthorized Access
```http
GET http://localhost:8082/api/v1/expenses/pending
```
**Expected Response**: `401 Unauthorized`

#### 5.2 Invalid Expense ID
```http
GET http://localhost:8082/api/v1/expenses/999999
Authorization: Bearer <your-jwt-token>
```
**Expected Response**: `404 Not Found`

#### 5.3 Invalid Approval Request
```http
POST http://localhost:8082/api/v1/expenses/approve
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "expenseId": null
}
```
**Expected Response**: `400 Bad Request` with validation errors

#### 5.4 Missing Rejection Reason
```http
POST http://localhost:8082/api/v1/expenses/reject
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "expenseId": 2
}
```
**Expected Response**: `400 Bad Request`

### Phase 6: Pagination Testing

#### 6.1 Large Page Size
```http
GET http://localhost:8082/api/v1/expenses/pending?page=0&size=100
Authorization: Bearer <your-jwt-token>
```

#### 6.2 Invalid Page Number
```http
GET http://localhost:8082/api/v1/expenses/pending?page=-1&size=10
Authorization: Bearer <your-jwt-token>
```

#### 6.3 Empty Results
```http
GET http://localhost:8082/api/v1/expenses/pending?page=999&size=10
Authorization: Bearer <your-jwt-token>
```

## Testing Workflow Scenarios

### Scenario 1: Complete Approval Workflow
1. Login as admin
2. Get pending expenses
3. Select first expense
4. Approve the expense
5. Verify expense status changed
6. Check currency totals updated

### Scenario 2: Complete Rejection Workflow
1. Login as admin
2. Get pending expenses
3. Select an expense
4. Reject with reason
5. Verify expense status and reason
6. Check totals remain unchanged

### Scenario 3: Report Generation
1. Login as admin
2. Approve/reject some expenses
3. Generate reports with different filters
4. Verify data consistency
5. Test pagination in reports

## Expected Response Codes

| Endpoint | Method | Success Code | Error Codes |
|----------|--------|--------------|-------------|
| `/auth/login` | POST | 200 | 401, 400 |
| `/expenses/pending` | GET | 200 | 401 |
| `/expenses/{id}` | GET | 200 | 401, 404 |
| `/expenses/approve` | POST | 200 | 401, 400, 404 |
| `/expenses/reject` | POST | 200 | 401, 400, 404 |
| `/expenses/totals/*` | GET | 200 | 401 |
| `/reports/expenses` | GET | 200 | 401, 400 |
| `/actuator/health` | GET | 200 | - |

## Common Issues & Solutions

### Issue 1: 401 Unauthorized
- **Cause**: Missing or invalid JWT token
- **Solution**: Ensure login first and include `Authorization: Bearer <token>` header

### Issue 2: 404 Not Found
- **Cause**: Invalid expense ID or endpoint
- **Solution**: Verify expense exists and URL is correct

### Issue 3: 400 Bad Request
- **Cause**: Invalid request body or missing required fields
- **Solution**: Check request format and required fields

### Issue 4: Connection Refused
- **Cause**: Application not running
- **Solution**: Start the application with `mvn spring-boot:run`

## Postman Collection Usage

1. Import the existing `Finance_Admin_Service.postman_collection.json`
2. Update base URL to `http://localhost:8082/api/v1`
3. Run "Admin Login" first to set JWT token
4. Execute other requests in sequence
5. Use collection variables for dynamic data

## Performance Testing

### Load Testing Endpoints
- `/expenses/pending` - Test with large datasets
- `/reports/expenses` - Test report generation performance
- `/expenses/totals/inr` - Test currency conversion performance

### Recommended Tools
- **Postman**: For functional testing
- **JMeter**: For load testing
- **curl**: For quick command-line testing

## Security Testing

1. **JWT Token Expiry**: Test with expired tokens
2. **Role-based Access**: Verify only FINANCE_ADMIN can access
3. **Input Validation**: Test with malicious inputs
4. **SQL Injection**: Test with SQL injection attempts
5. **XSS Prevention**: Test with script injections

## Automation Testing

Create automated test suites for:
- **Smoke Tests**: Basic functionality
- **Regression Tests**: After code changes
- **Integration Tests**: End-to-end workflows
- **Performance Tests**: Response time validation

---

**Testing Checklist**:
- [ ] All endpoints return expected status codes
- [ ] Authentication works correctly
- [ ] Expense approval/rejection workflow functions
- [ ] Reports generate accurate data
- [ ] Error handling works properly
- [ ] Pagination works correctly
- [ ] Currency conversion is accurate
- [ ] Email notifications are sent (check logs)