# Complete Manual API Testing Steps

## Prerequisites
1. **Start Application**: `java -jar target\finance-admin-service-1.0.0.jar`
2. **Wait**: 30 seconds for application startup
3. **Base URL**: `http://localhost:8082/api/v1`

---

## Phase 1: Application Health & Setup

### Step 1.1: Health Check
```bash
curl -X GET http://localhost:8082/api/v1/actuator/health
```
**Expected**: `200 OK` with status "UP"

### Step 1.2: Application Info
```bash
curl -X GET http://localhost:8082/api/v1/actuator/info
```
**Expected**: `200 OK` with application details

---

## Phase 2: Authentication Testing

### Step 2.1: Valid Admin Login
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"password\"}"
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

### Step 2.2: Alternative Admin Login
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"finance.manager\",\"password\":\"password\"}"
```
**Expected**: `200 OK` with JWT token

### Step 2.3: Invalid Login Test
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"invalid\",\"password\":\"wrong\"}"
```
**Expected**: `401 Unauthorized`

---

## Phase 3: Expense Management Testing

**Note**: Replace `<JWT_TOKEN>` with the actual token from login

### Step 3.1: Get Pending Expenses
```bash
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with paginated expense list

### Step 3.2: Get Specific Expense
```bash
curl -X GET http://localhost:8082/api/v1/expenses/1 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with expense details

### Step 3.3: Approve Expense
```bash
curl -X POST http://localhost:8082/api/v1/expenses/approve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d "{\"expenseId\":1}"
```
**Expected**: `200 OK` with updated expense status "APPROVED"

### Step 3.4: Reject Expense
```bash
curl -X POST http://localhost:8082/api/v1/expenses/reject \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d "{\"expenseId\":3,\"rejectionReason\":\"Receipt is not clear\"}"
```
**Expected**: `200 OK` with updated expense status "REJECTED"

### Step 3.5: Get Currency Totals
```bash
curl -X GET http://localhost:8082/api/v1/expenses/totals/currency \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with currency-wise totals array

### Step 3.6: Get INR Total
```bash
curl -X GET http://localhost:8082/api/v1/expenses/totals/inr \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with total amount in INR

### Step 3.7: Sync Expense
```bash
curl -X POST http://localhost:8082/api/v1/expenses/1/sync \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with synced expense data

---

## Phase 4: Reports Testing

### Step 4.1: Multi-Employee Report
```bash
curl -X GET "http://localhost:8082/api/v1/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with paginated report data

### Step 4.2: Employee-Specific Report
```bash
curl -X GET "http://localhost:8082/api/v1/reports/expenses/employee/1?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with employee-specific report

### Step 4.3: Date Range Only Report
```bash
curl -X GET "http://localhost:8082/api/v1/reports/expenses?startDate=2024-01-01&endDate=2024-01-31&page=0&size=5" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with date-filtered report

### Step 4.4: Default Report (No Filters)
```bash
curl -X GET "http://localhost:8082/api/v1/reports/expenses?page=0&size=5" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with default date range report

---

## Phase 5: Error Handling Testing

### Step 5.1: Unauthorized Access
```bash
curl -X GET http://localhost:8082/api/v1/expenses/pending
```
**Expected**: `401 Unauthorized`

### Step 5.2: Invalid Expense ID
```bash
curl -X GET http://localhost:8082/api/v1/expenses/999999 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `404 Not Found`

### Step 5.3: Invalid Approval Request
```bash
curl -X POST http://localhost:8082/api/v1/expenses/approve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d "{\"expenseId\":null}"
```
**Expected**: `400 Bad Request` with validation errors

### Step 5.4: Missing Rejection Reason
```bash
curl -X POST http://localhost:8082/api/v1/expenses/reject \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d "{\"expenseId\":2}"
```
**Expected**: `400 Bad Request`

### Step 5.5: Invalid JWT Token
```bash
curl -X GET http://localhost:8082/api/v1/expenses/pending \
  -H "Authorization: Bearer invalid_token"
```
**Expected**: `401 Unauthorized`

---

## Phase 6: Pagination Testing

### Step 6.1: Large Page Size
```bash
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=0&size=100" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with up to 100 records

### Step 6.2: Invalid Page Number
```bash
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=-1&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `400 Bad Request` or empty results

### Step 6.3: Empty Results Page
```bash
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=999&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `200 OK` with empty content array

---

## Phase 7: Performance Testing

### Step 7.1: Response Time Test
```bash
curl -w "Total Time: %{time_total}s\nResponse Code: %{http_code}\n" \
  -X GET http://localhost:8082/api/v1/expenses/pending \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -o /dev/null -s
```
**Expected**: Response time < 3 seconds

### Step 7.2: Concurrent Requests Test
Run multiple curl commands simultaneously:
```bash
# Terminal 1
curl -X GET http://localhost:8082/api/v1/expenses/pending -H "Authorization: Bearer <JWT_TOKEN>"

# Terminal 2 (simultaneously)
curl -X GET http://localhost:8082/api/v1/expenses/totals/currency -H "Authorization: Bearer <JWT_TOKEN>"

# Terminal 3 (simultaneously)
curl -X GET "http://localhost:8082/api/v1/reports/expenses?page=0&size=5" -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: All requests complete successfully

---

## Phase 8: Security Testing

### Step 8.1: SQL Injection Test
```bash
curl -X GET "http://localhost:8082/api/v1/expenses/1'; DROP TABLE expenses; --" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
**Expected**: `404 Not Found` (no SQL injection)

### Step 8.2: XSS Test
```bash
curl -X POST http://localhost:8082/api/v1/expenses/reject \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d "{\"expenseId\":2,\"rejectionReason\":\"<script>alert('xss')</script>\"}"
```
**Expected**: Request processed safely (no script execution)

---

## Phase 9: Business Logic Testing

### Step 9.1: Approve Already Approved Expense
1. First approve an expense (Step 3.3)
2. Try to approve the same expense again
**Expected**: Appropriate handling (success or business rule error)

### Step 9.2: Reject Already Rejected Expense
1. First reject an expense (Step 3.4)
2. Try to reject the same expense again
**Expected**: Appropriate handling (success or business rule error)

### Step 9.3: Workflow State Validation
Test the complete workflow:
1. Get pending expense
2. Approve it
3. Verify it's no longer in pending list
4. Check totals are updated

---

## Phase 10: Integration Testing

### Step 10.1: Email Notification Test
After approving/rejecting expenses, check application logs for email sending:
```bash
# Check logs (if application is running in foreground)
# Look for email-related log entries
```

### Step 10.2: Currency Conversion Test
1. Get currency totals
2. Get INR total
3. Verify conversion calculations are correct

---

## Test Results Validation

### Success Criteria
- [ ] All health checks return 200
- [ ] Authentication works for valid credentials
- [ ] All CRUD operations work correctly
- [ ] Proper error codes for invalid requests
- [ ] Pagination works correctly
- [ ] Reports generate successfully
- [ ] Security measures are effective
- [ ] Performance is acceptable (< 3s response time)

### Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Connection refused | App not running | Start application |
| 401 Unauthorized | Missing/invalid token | Login and use valid JWT |
| 404 Not Found | Invalid endpoint/ID | Check URL and resource ID |
| 400 Bad Request | Invalid request body | Validate JSON format |
| 500 Internal Error | Application error | Check logs and database |

---

## Cleanup

### Stop Application
```bash
# Find Java process
tasklist | findstr java

# Kill process (replace PID)
taskkill /PID <process_id> /F
```

### Test Data Reset
If needed, restart application to reset test data to initial state.

---

**Testing Complete!** 

All endpoints tested systematically with proper validation of responses, error handling, and business logic.