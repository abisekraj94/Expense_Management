@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Finance Admin Service - Complete API Testing
echo ========================================
echo.

set BASE_URL=http://localhost:8082/api/v1
set JWT_TOKEN=
set TEST_RESULTS=api_test_results.txt

echo Starting API Testing at %date% %time% > %TEST_RESULTS%
echo Base URL: %BASE_URL% >> %TEST_RESULTS%
echo. >> %TEST_RESULTS%

echo Step 1: Starting Application...
echo ----------------------------------------
start /b java -jar target\finance-admin-service-1.0.0.jar
echo Application starting in background...
echo Waiting 30 seconds for application to start...
timeout /t 30 /nobreak > nul

echo.
echo Step 2: Health Check
echo ----------------------------------------
curl -s -w "Response Code: %%{http_code}\nResponse Time: %%{time_total}s\n" %BASE_URL%/actuator/health
echo Health Check completed >> %TEST_RESULTS%

echo.
echo Step 3: Application Info
echo ----------------------------------------
curl -s -w "Response Code: %%{http_code}\n" %BASE_URL%/actuator/info
echo Application Info checked >> %TEST_RESULTS%

echo.
echo Step 4: Authentication Testing
echo ----------------------------------------
echo Testing Admin Login...

rem Create temporary file for login response
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password\"}" %BASE_URL%/auth/login > temp_login.json

rem Check if login was successful
findstr "accessToken" temp_login.json > nul
if !errorlevel! == 0 (
    echo ✓ Login successful
    echo Login successful >> %TEST_RESULTS%
    
    rem Extract token (simplified extraction)
    for /f "tokens=2 delims=:" %%a in ('findstr "accessToken" temp_login.json') do (
        set TOKEN_PART=%%a
        set TOKEN_PART=!TOKEN_PART:"=!
        set TOKEN_PART=!TOKEN_PART:,=!
        set JWT_TOKEN=!TOKEN_PART: =!
    )
    echo Token extracted: !JWT_TOKEN:~0,20!...
) else (
    echo ✗ Login failed
    echo Login failed >> %TEST_RESULTS%
    type temp_login.json
)

echo.
echo Step 5: Invalid Login Test
echo ----------------------------------------
curl -s -w "Response Code: %%{http_code}\n" -X POST -H "Content-Type: application/json" -d "{\"username\":\"invalid\",\"password\":\"wrong\"}" %BASE_URL%/auth/login
echo Invalid login test completed >> %TEST_RESULTS%

echo.
echo Step 6: Expense Management Testing
echo ----------------------------------------

if defined JWT_TOKEN (
    echo Testing with JWT Token...
    
    echo 6.1: Get Pending Expenses
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/pending?page=0^&size=10
    echo Pending expenses test completed >> %TEST_RESULTS%
    
    echo.
    echo 6.2: Get Specific Expense
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/1
    echo Specific expense test completed >> %TEST_RESULTS%
    
    echo.
    echo 6.3: Get Currency Totals
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/totals/currency
    echo Currency totals test completed >> %TEST_RESULTS%
    
    echo.
    echo 6.4: Get INR Total
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/totals/inr
    echo INR total test completed >> %TEST_RESULTS%
    
    echo.
    echo 6.5: Approve Expense Test
    curl -s -w "Response Code: %%{http_code}\n" -X POST -H "Content-Type: application/json" -H "Authorization: Bearer !JWT_TOKEN!" -d "{\"expenseId\":1}" %BASE_URL%/expenses/approve
    echo Approve expense test completed >> %TEST_RESULTS%
    
    echo.
    echo 6.6: Reject Expense Test
    curl -s -w "Response Code: %%{http_code}\n" -X POST -H "Content-Type: application/json" -H "Authorization: Bearer !JWT_TOKEN!" -d "{\"expenseId\":3,\"rejectionReason\":\"Test rejection\"}" %BASE_URL%/expenses/reject
    echo Reject expense test completed >> %TEST_RESULTS%
    
) else (
    echo ✗ Skipping authenticated tests - no JWT token available
    echo Skipping authenticated tests - no JWT token >> %TEST_RESULTS%
)

echo.
echo Step 7: Reports Testing
echo ----------------------------------------

if defined JWT_TOKEN (
    echo 7.1: Generate Multi-Employee Report
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" "%BASE_URL%/reports/expenses?employeeIds=1,2,3&startDate=2024-01-01&endDate=2024-01-31&page=0&size=5"
    echo Multi-employee report test completed >> %TEST_RESULTS%
    
    echo.
    echo 7.2: Employee-Specific Report
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" "%BASE_URL%/reports/expenses/employee/1?startDate=2024-01-01&endDate=2024-01-31"
    echo Employee-specific report test completed >> %TEST_RESULTS%
    
    echo.
    echo 7.3: Date Range Report
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" "%BASE_URL%/reports/expenses?startDate=2024-01-01&endDate=2024-01-31&page=0&size=5"
    echo Date range report test completed >> %TEST_RESULTS%
    
) else (
    echo ✗ Skipping report tests - no JWT token available
    echo Skipping report tests - no JWT token >> %TEST_RESULTS%
)

echo.
echo Step 8: Error Handling Tests
echo ----------------------------------------

echo 8.1: Unauthorized Access Test
curl -s -w "Response Code: %%{http_code}\n" %BASE_URL%/expenses/pending
echo Unauthorized access test completed >> %TEST_RESULTS%

echo.
echo 8.2: Invalid Expense ID Test
if defined JWT_TOKEN (
    curl -s -w "Response Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/999999
    echo Invalid expense ID test completed >> %TEST_RESULTS%
) else (
    echo Skipped - no JWT token
)

echo.
echo 8.3: Invalid Request Body Test
if defined JWT_TOKEN (
    curl -s -w "Response Code: %%{http_code}\n" -X POST -H "Content-Type: application/json" -H "Authorization: Bearer !JWT_TOKEN!" -d "{\"expenseId\":null}" %BASE_URL%/expenses/approve
    echo Invalid request body test completed >> %TEST_RESULTS%
) else (
    echo Skipped - no JWT token
)

echo.
echo Step 9: Performance Tests
echo ----------------------------------------

if defined JWT_TOKEN (
    echo 9.1: Response Time Test - Pending Expenses
    curl -s -w "Total Time: %%{time_total}s\nResponse Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/pending > nul
    
    echo 9.2: Response Time Test - Currency Totals
    curl -s -w "Total Time: %%{time_total}s\nResponse Code: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/totals/currency > nul
    
    echo Performance tests completed >> %TEST_RESULTS%
) else (
    echo Skipped - no JWT token
)

echo.
echo Step 10: Cleanup
echo ----------------------------------------
del temp_login.json 2>nul

echo.
echo ========================================
echo API Testing Complete!
echo ========================================
echo.
echo Test Results Summary:
echo - Health Check: Completed
echo - Authentication: %JWT_TOKEN:~0,10%...
echo - Expense Management: Tested
echo - Reports: Tested  
echo - Error Handling: Tested
echo - Performance: Tested
echo.
echo Detailed results saved to: %TEST_RESULTS%
echo.
echo To stop the application, find the Java process and terminate it.
echo Use: tasklist | findstr java
echo Then: taskkill /PID [process_id] /F
echo.
pause