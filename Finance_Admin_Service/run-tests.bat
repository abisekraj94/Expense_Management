@echo off
echo Running Finance Admin Service Unit Tests...
echo.

echo 1. Running all tests...
mvn clean test

echo.
echo 2. Generating test coverage report...
mvn jacoco:report

echo.
echo 3. Running specific service tests...
mvn test -Dtest=*ServiceTest

echo.
echo 4. Running controller tests...
mvn test -Dtest=*ControllerTest

echo.
echo Test execution completed!
echo Check target/site/jacoco/index.html for coverage report
pause