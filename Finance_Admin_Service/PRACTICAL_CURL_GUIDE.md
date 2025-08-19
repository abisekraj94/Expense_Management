# Practical Curl Testing Guide

## Quick Setup
1. **Start Application**: `java -jar target\finance-admin-service-1.0.0.jar`
2. **Wait 30 seconds** for startup
3. **Open Command Prompt** and run commands below

---

## Step-by-Step Execution

### 1. Health Check
```cmd
curl -X GET http://localhost:8082/api/v1/actuator/health
```
✅ **Expected**: `{"status":"UP"}`

### 2. Login (Get JWT Token)
```cmd
curl -X POST http://localhost:8082/api/v1/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password\"}"
```
✅ **Expected**: JSON with `accessToken` field
📝 **Action**: Copy the token value (without quotes)

### 3. Set Token Variable (Windows)
```cmd
set TOKEN=YOUR_COPIED_TOKEN_HERE
```

### 4. Get Pending Expenses
```cmd
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=0&size=10" -H "Authorization: Bearer %TOKEN%"
```
✅ **Expected**: Paginated expense list

### 5. Get Specific Expense
```cmd
curl -X GET http://localhost:8082/api/v1/expenses/1 -H "Authorization: Bearer %TOKEN%"
```
✅ **Expected**: Single expense details

### 6. Approve Expense
```cmd
curl -X POST http://localhost:8082/api/v1/expenses/approve -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"expenseId\":1}"
```
✅ **Expected**: Updated expense with APPROVED status

### 7. Reject Expense
```cmd
curl -X POST http://localhost:8082/api/v1/expenses/reject -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"expenseId\":3,\"rejectionReason\":\"Test rejection\"}"
```
✅ **Expected**: Updated expense with REJECTED status

### 8. Get Currency Totals
```cmd
curl -X GET http://localhost:8082/api/v1/expenses/totals/currency -H "Authorization: Bearer %TOKEN%"
```
✅ **Expected**: Array of currency totals

### 9. Get INR Total
```cmd
curl -X GET http://localhost:8082/api/v1/expenses/totals/inr -H "Authorization: Bearer %TOKEN%"
```
✅ **Expected**: Numeric total in INR

### 10. Generate Report
```cmd
curl -X GET "http://localhost:8082/api/v1/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5" -H "Authorization: Bearer %TOKEN%"
```
✅ **Expected**: Paginated report data

---

## Error Testing

### Unauthorized Access
```cmd
curl -X GET http://localhost:8082/api/v1/expenses/pending
```
❌ **Expected**: 401 Unauthorized

### Invalid Login
```cmd
curl -X POST http://localhost:8082/api/v1/auth/login -H "Content-Type: application/json" -d "{\"username\":\"wrong\",\"password\":\"wrong\"}"
```
❌ **Expected**: 401 Unauthorized

### Invalid Expense ID
```cmd
curl -X GET http://localhost:8082/api/v1/expenses/999999 -H "Authorization: Bearer %TOKEN%"
```
❌ **Expected**: 404 Not Found

---

## Complete Test Sequence

**Copy and paste this entire sequence:**

```cmd
rem Health Check
curl -X GET http://localhost:8082/api/v1/actuator/health

rem Login and extract token manually
curl -X POST http://localhost:8082/api/v1/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password\"}"

rem Set your token here (replace with actual token)
set TOKEN=YOUR_TOKEN_HERE

rem Test all endpoints
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=0&size=10" -H "Authorization: Bearer %TOKEN%"
curl -X GET http://localhost:8082/api/v1/expenses/1 -H "Authorization: Bearer %TOKEN%"
curl -X GET http://localhost:8082/api/v1/expenses/totals/currency -H "Authorization: Bearer %TOKEN%"
curl -X GET http://localhost:8082/api/v1/expenses/totals/inr -H "Authorization: Bearer %TOKEN%"
curl -X GET "http://localhost:8082/api/v1/reports/expenses?page=0&size=5" -H "Authorization: Bearer %TOKEN%"

rem Test error cases
curl -X GET http://localhost:8082/api/v1/expenses/pending
curl -X POST http://localhost:8082/api/v1/auth/login -H "Content-Type: application/json" -d "{\"username\":\"wrong\",\"password\":\"wrong\"}"
```

---

## Tips

1. **Token Extraction**: Look for `"accessToken":"eyJ..."` in login response
2. **Windows Escaping**: Use `^` for line continuation in batch files
3. **URL Encoding**: Use quotes around URLs with query parameters
4. **JSON Format**: Ensure proper JSON escaping in request bodies
5. **Response Codes**: Check HTTP status codes (200=success, 401=unauthorized, 404=not found)

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Start application first |
| 401 Unauthorized | Check JWT token is set correctly |
| Invalid JSON | Verify request body format |
| Command not found | Install curl or use Git Bash |