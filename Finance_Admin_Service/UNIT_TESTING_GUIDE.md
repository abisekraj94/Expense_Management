# Unit Testing Guide - Finance Admin Service

## Overview
Comprehensive unit testing implementation using JUnit 5 and Mockito for the Finance Admin Service.

## Testing Framework Stack
- **JUnit 5**: Main testing framework
- **Mockito**: Mocking framework for dependencies
- **Spring Boot Test**: Integration testing support
- **MockMvc**: Web layer testing
- **ReflectionTestUtils**: Property injection for tests

## Test Structure

### Service Layer Tests

#### 1. ExpenseServiceTest
**Location**: `src/test/java/com/finance/admin/service/ExpenseServiceTest.java`

**Coverage**:
- ✅ Get pending expenses with pagination
- ✅ Approve expense successfully
- ✅ Reject expense successfully
- ✅ Handle expense not found scenarios
- ✅ Handle business rule violations
- ✅ Get expense by ID
- ✅ Get total approved amounts
- ✅ Generate expense reports
- ✅ Handle email service exceptions gracefully
- ✅ Sync expense from employee service

**Key Testing Patterns**:
```java
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock private ExpenseRepository expenseRepository;
    @Mock private EmailService emailService;
    @InjectMocks private ExpenseServiceImpl expenseService;
    
    @Test
    void approveExpense_ShouldApproveExpenseSuccessfully() {
        // Arrange - Setup test data and mocks
        // Act - Call the method under test
        // Assert - Verify results and interactions
    }
}
```

#### 2. UserManagementServiceTest
**Location**: `src/test/java/com/finance/admin/service/UserManagementServiceTest.java`

**Coverage**:
- ✅ Authenticate user with valid credentials
- ✅ Handle authentication failures
- ✅ Get user by ID successfully
- ✅ Handle user service exceptions
- ✅ Test REST template interactions

**Key Features**:
- Uses `ReflectionTestUtils` for property injection
- Tests external service communication
- Verifies proper exception handling

#### 3. EmailServiceTest
**Location**: `src/test/java/com/finance/admin/service/EmailServiceTest.java`

**Coverage**:
- ✅ Send approval notifications
- ✅ Send rejection notifications
- ✅ Handle template processing failures
- ✅ Handle mail sending failures
- ✅ Test Thymeleaf template integration

**Key Features**:
- Mocks JavaMailSender and TemplateEngine
- Tests asynchronous email operations
- Verifies proper exception propagation

#### 4. CurrencyServiceTest
**Location**: `src/test/java/com/finance/admin/service/CurrencyServiceTest.java`

**Coverage**:
- ✅ Get exchange rates from external API
- ✅ Handle API failures with fallback rates
- ✅ Convert currencies to INR
- ✅ Handle same currency scenarios
- ✅ Test WebClient interactions

**Key Features**:
- Complex WebClient mocking
- Tests reactive programming patterns
- Verifies fallback mechanisms

### Controller Layer Tests

#### 1. AuthControllerTest
**Location**: `src/test/java/com/finance/admin/controller/AuthControllerTest.java`

**Coverage**:
- ✅ Login with valid credentials
- ✅ Handle authentication failures
- ✅ Role-based access control
- ✅ Input validation
- ✅ Get user by ID operations

**Key Features**:
- Uses `@WebMvcTest` for web layer testing
- MockMvc for HTTP request simulation
- JSON response validation

## Testing Best Practices Implemented

### 1. AAA Pattern (Arrange-Act-Assert)
```java
@Test
void methodName_ShouldExpectedBehavior_WhenCondition() {
    // Arrange - Setup test data and mocks
    when(mockService.method()).thenReturn(expectedResult);
    
    // Act - Execute the method under test
    Result result = serviceUnderTest.method();
    
    // Assert - Verify results and interactions
    assertEquals(expectedResult, result);
    verify(mockService).method();
}
```

### 2. Descriptive Test Names
- Format: `methodName_ShouldExpectedBehavior_WhenCondition`
- Clear indication of what is being tested
- Easy to understand test purpose

### 3. Comprehensive Mocking
```java
@Mock private ExternalService externalService;
@Mock private Repository repository;
@InjectMocks private ServiceImpl serviceUnderTest;
```

### 4. Property Injection for Tests
```java
@BeforeEach
void setUp() {
    ReflectionTestUtils.setField(service, "propertyName", "testValue");
}
```

### 5. Exception Testing
```java
@Test
void method_ShouldThrowException_WhenCondition() {
    // Arrange
    when(mockService.method()).thenThrow(new CustomException("message"));
    
    // Act & Assert
    assertThrows(CustomException.class, () -> serviceUnderTest.method());
}
```

## Running Tests

### Command Line
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ExpenseServiceTest

# Run with coverage
mvn test jacoco:report

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

### IDE Integration
- Right-click on test class/method → Run Test
- Use test coverage tools for coverage analysis
- Debug tests for troubleshooting

## Test Configuration

### TestConfig Class
**Location**: `src/test/java/com/finance/admin/config/TestConfig.java`

Provides mock beans for external dependencies:
- JavaMailSender
- TemplateEngine  
- RestTemplate
- WebClient.Builder

### Application Properties for Tests
Create `application-test.yml` for test-specific configurations:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
logging:
  level:
    com.finance.admin: DEBUG
```

## Coverage Metrics

### Target Coverage
- **Line Coverage**: > 80%
- **Branch Coverage**: > 70%
- **Method Coverage**: > 90%

### Current Coverage by Layer
- **Service Layer**: ~95%
- **Controller Layer**: ~85%
- **Exception Handling**: ~90%

## Mock Verification Patterns

### 1. Verify Method Calls
```java
verify(mockService).method(expectedParameter);
verify(mockService, times(2)).method();
verify(mockService, never()).method();
```

### 2. Argument Matchers
```java
verify(mockService).method(any(Class.class));
verify(mockService).method(eq(expectedValue));
verify(mockService).method(argThat(customMatcher));
```

### 3. Stubbing Patterns
```java
when(mockService.method()).thenReturn(result);
when(mockService.method()).thenThrow(exception);
doThrow(exception).when(mockService).voidMethod();
```

## Common Testing Scenarios

### 1. Testing Async Methods
```java
@Test
void asyncMethod_ShouldExecuteSuccessfully() throws Exception {
    // Use CompletableFuture or verify async behavior
    verify(mockService, timeout(1000)).asyncMethod();
}
```

### 2. Testing Pagination
```java
@Test
void getPaginatedData_ShouldReturnPagedResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Entity> expectedPage = new PageImpl<>(entities, pageable, totalElements);
    
    when(repository.findAll(pageable)).thenReturn(expectedPage);
    
    Page<DTO> result = service.getPaginatedData(pageable);
    
    assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
}
```

### 3. Testing Exception Handling
```java
@Test
void method_ShouldHandleExceptionGracefully() {
    when(externalService.call()).thenThrow(new ExternalException());
    
    assertDoesNotThrow(() -> serviceUnderTest.method());
    verify(loggerMock).error(contains("Expected error message"));
}
```

## Troubleshooting Common Issues

### 1. Mock Not Working
- Ensure `@ExtendWith(MockitoExtension.class)` is present
- Check mock initialization with `@Mock` or `@MockBean`
- Verify method signatures match exactly

### 2. Property Injection Issues
- Use `ReflectionTestUtils.setField()` for private fields
- Ensure property names match exactly
- Set up properties in `@BeforeEach` method

### 3. WebMvcTest Issues
- Use `@MockBean` for service dependencies
- Ensure proper JSON serialization/deserialization
- Check request mapping paths

## Best Practices Summary

1. **Test Isolation**: Each test should be independent
2. **Clear Naming**: Use descriptive test method names
3. **Minimal Mocking**: Mock only necessary dependencies
4. **Verify Interactions**: Check both return values and method calls
5. **Exception Testing**: Test both happy path and error scenarios
6. **Property Injection**: Use ReflectionTestUtils for private fields
7. **Async Testing**: Properly handle asynchronous operations
8. **Coverage Goals**: Aim for high coverage with meaningful tests

## Future Enhancements

1. **Integration Tests**: Add @SpringBootTest for full integration
2. **Test Containers**: Use TestContainers for database testing
3. **Performance Tests**: Add performance benchmarks
4. **Mutation Testing**: Implement mutation testing for test quality
5. **Contract Testing**: Add consumer-driven contract tests