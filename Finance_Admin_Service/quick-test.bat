@echo off
echo ========================================
echo Finance Admin Service - Quick API Test
echo ========================================
echo.

set BASE_URL=http://localhost:8082/api/v1

echo Step 1: Starting Application...
echo ----------------------------------------
echo Starting application in background...
start /b java -jar target\finance-admin-service-1.0.0.jar

echo Waiting 30 seconds for startup...
timeout /t 30 /nobreak > nul

echo.
echo Step 2: Testing Health Check...
echo ----------------------------------------
curl -s -w "Status: %%{http_code} | Time: %%{time_total}s\n" %BASE_URL%/actuator/health

echo.
echo Step 3: Testing Authentication...
echo ----------------------------------------
echo Attempting admin login...
curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password\"}" %BASE_URL%/auth/login > login_response.json

findstr "accessToken" login_response.json > nul
if %errorlevel% == 0 (
    echo ✓ Login successful - JWT token received
    
    rem Extract token for further tests
    for /f "tokens=2 delims=:" %%a in ('findstr "accessToken" login_response.json') do (
        set "token_line=%%a"
        setlocal enabledelayedexpansion
        set "token_clean=!token_line:"=!"
        set "token_clean=!token_clean:,=!"
        set "JWT_TOKEN=!token_clean: =!"
        
        echo.
        echo Step 4: Testing Protected Endpoints...
        echo ----------------------------------------
        
        echo Testing pending expenses...
        curl -s -w "Status: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/pending?page=0^&size=5
        
        echo.
        echo Testing currency totals...
        curl -s -w "Status: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/expenses/totals/currency
        
        echo.
        echo Testing reports...
        curl -s -w "Status: %%{http_code}\n" -H "Authorization: Bearer !JWT_TOKEN!" %BASE_URL%/reports/expenses?page=0^&size=3
        
        endlocal
    )
) else (
    echo ✗ Login failed
    type login_response.json
)

echo.
echo Step 5: Testing Error Handling...
echo ----------------------------------------
echo Testing unauthorized access...
curl -s -w "Status: %%{http_code}\n" %BASE_URL%/expenses/pending

echo.
echo Testing invalid login...
curl -s -w "Status: %%{http_code}\n" -X POST -H "Content-Type: application/json" -d "{\"username\":\"invalid\",\"password\":\"wrong\"}" %BASE_URL%/auth/login

echo.
echo ========================================
echo Quick Test Complete!
echo ========================================
echo.
echo Results Summary:
echo - Health Check: Tested
echo - Authentication: Tested
echo - Protected Endpoints: Tested
echo - Error Handling: Tested
echo.
echo For detailed testing, use:
echo 1. complete-api-test.bat (comprehensive)
echo 2. MANUAL_API_TESTING_STEPS.md (manual)
echo 3. Postman collection (automated)
echo.

del login_response.json 2>nul

echo To stop the application:
echo tasklist ^| findstr java
echo taskkill /PID [process_id] /F
echo.
pause