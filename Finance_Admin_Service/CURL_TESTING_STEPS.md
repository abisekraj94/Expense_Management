# Finance Admin Service - Curl Testing Steps

## Prerequisites
1. Start application: `java -jar target\finance-admin-service-1.0.0.jar`
2. Base URL: `http://localhost:8082/api/v1`

---

## Step 1: Health Check
```bash
curl -X GET http://localhost:8082/api/v1/actuator/health
```
**Expected**: `{"status":"UP"}`

---

## Step 2: Login & Get JWT Token
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"password\"}"
```
**Save the `accessToken` from response for next steps**

---

## Step 3: Get Pending Expenses
```bash
curl -X GET "http://localhost:8082/api/v1/expenses/pending?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Step 4: Get Specific Expense
```bash
curl -X GET http://localhost:8082/api/v1/expenses/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Step 5: Approve Expense
```bash
curl -X POST http://localhost:8082/api/v1/expenses/approve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d "{\"expenseId\":1}"
```

---

## Step 6: Reject Expense
```bash
curl -X POST http://localhost:8082/api/v1/expenses/reject \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d "{\"expenseId\":3,\"rejectionReason\":\"Invalid receipt\"}"
```

---

## Step 7: Get Currency Totals
```bash
curl -X GET http://localhost:8082/api/v1/expenses/totals/currency \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Step 8: Get INR Total
```bash
curl -X GET http://localhost:8082/api/v1/expenses/totals/inr \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Step 9: Generate Report
```bash
curl -X GET "http://localhost:8082/api/v1/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Step 10: Test Error Cases

### Unauthorized Access
```bash
curl -X GET http://localhost:8082/api/v1/expenses/pending
```
**Expected**: 401 Unauthorized

### Invalid Login
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"invalid\",\"password\":\"wrong\"}"
```
**Expected**: 401 Unauthorized

### Invalid Expense ID
```bash
curl -X GET http://localhost:8082/api/v1/expenses/999999 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```
**Expected**: 404 Not Found