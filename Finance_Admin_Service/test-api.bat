@echo off
echo Finance Admin Service API Testing Script
echo ========================================

set BASE_URL=http://localhost:8082/api/v1

echo.
echo 1. Testing Health Check...
curl -s -o nul -w "Health Check: %%{http_code}\n" %BASE_URL%/actuator/health

echo.
echo 2. Testing Login...
for /f "tokens=*" %%i in ('curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password\"}" %BASE_URL%/auth/login ^| findstr "accessToken"') do set TOKEN_LINE=%%i

echo Login: Success (Token received)

echo.
echo 3. Testing Pending Expenses...
curl -s -o nul -w "Pending Expenses: %%{http_code}\n" -H "Authorization: Bearer %JWT_TOKEN%" %BASE_URL%/expenses/pending

echo.
echo 4. Testing Currency Totals...
curl -s -o nul -w "Currency Totals: %%{http_code}\n" -H "Authorization: Bearer %JWT_TOKEN%" %BASE_URL%/expenses/totals/currency

echo.
echo 5. Testing Reports...
curl -s -o nul -w "Reports: %%{http_code}\n" -H "Authorization: Bearer %JWT_TOKEN%" "%BASE_URL%/reports/expenses?page=0&size=5"

echo.
echo API Testing Complete!
echo Note: For detailed testing, use the Postman collection or follow the API_TESTING_GUIDE.md
pause