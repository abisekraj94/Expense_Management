# Test Execution Commands

## Basic Test Commands

### Run All Tests
```bash
mvn clean test
```

### Run Tests with Coverage Report
```bash
mvn clean test jacoco:report
```

### Run Specific Test Class
```bash
mvn test -Dtest=ExpenseServiceTest
mvn test -Dtest=UserManagementServiceTest
mvn test -Dtest=EmailServiceTest
mvn test -Dtest=CurrencyServiceTest
mvn test -Dtest=AuthControllerTest
```

### Run Tests by Pattern
```bash
# Run all service tests
mvn test -Dtest=*ServiceTest

# Run all controller tests  
mvn test -Dtest=*ControllerTest

# Run specific test method
mvn test -Dtest=ExpenseServiceTest#approveExpense_ShouldApproveExpenseSuccessfully
```

## Advanced Test Commands

### Run Tests with Profiles
```bash
mvn test -Dspring.profiles.active=test
```

### Run Tests with Debug
```bash
mvn test -Dmaven.surefire.debug
```

### Skip Tests
```bash
mvn clean compile -DskipTests
```

### Run Tests in Parallel
```bash
mvn test -Dparallel=methods -DthreadCount=4
```

## Coverage Reports

### Generate Coverage Report
```bash
mvn jacoco:report
```

### View Coverage Report
Open: `target/site/jacoco/index.html`

### Coverage Thresholds
- Line Coverage: > 80%
- Branch Coverage: > 70%
- Method Coverage: > 90%

## Test Results

### Surefire Reports
Location: `target/surefire-reports/`

### Test Output
```bash
# Verbose output
mvn test -Dtest.verbose=true

# Show stack traces
mvn test -Dsurefire.printSummary=true
```