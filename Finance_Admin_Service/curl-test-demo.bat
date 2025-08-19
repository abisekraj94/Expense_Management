@echo off
echo Finance Admin Service - Curl Testing Demo
echo ==========================================

set BASE_URL=http://localhost:8082/api/v1

echo.
echo Step 1: Health Check
echo --------------------
curl -X GET %BASE_URL%/actuator/health
echo.

echo.
echo Step 2: Admin Login
echo -------------------
curl -X POST %BASE_URL%/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"password\"}"
echo.

echo.
echo COPY THE ACCESS TOKEN FROM ABOVE RESPONSE
echo Then run the following commands with your token:
echo.

echo Step 3: Get Pending Expenses
echo curl -X GET "%BASE_URL%/expenses/pending?page=0&size=10" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
echo.

echo Step 4: Get Specific Expense
echo curl -X GET %BASE_URL%/expenses/1 ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
echo.

echo Step 5: Approve Expense
echo curl -X POST %BASE_URL%/expenses/approve ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -d "{\"expenseId\":1}"
echo.

echo Step 6: Reject Expense
echo curl -X POST %BASE_URL%/expenses/reject ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -d "{\"expenseId\":3,\"rejectionReason\":\"Invalid receipt\"}"
echo.

echo Step 7: Get Currency Totals
echo curl -X GET %BASE_URL%/expenses/totals/currency ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
echo.

echo Step 8: Generate Report
echo curl -X GET "%BASE_URL%/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
echo.

echo Step 9: Test Unauthorized Access
echo ----------------------------------
curl -X GET %BASE_URL%/expenses/pending
echo.

echo Step 10: Test Invalid Login
echo -----------------------------
curl -X POST %BASE_URL%/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"invalid\",\"password\":\"wrong\"}"
echo.

pause