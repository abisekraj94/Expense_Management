# Unit Testing Guide for User Management Service

This guide explains how to implement comprehensive unit tests for the service layer using JUnit 5 and Mockito.

## Overview

The service layer unit tests focus on testing business logic in isolation by mocking all external dependencies. This ensures fast, reliable tests that verify the core functionality without requiring database connections or external services.

## Test Structure

### 1. Test Classes Created

- **UserServiceImplTest**: Tests for the main user management service
- **MicroserviceIntegrationServiceImplTest**: Tests for microservice communication
- **TestConfig**: Test configuration for shared beans
- **TestDataBuilder**: Utility class for creating test data

### 2. Testing Framework

- **JUnit 5**: Modern testing framework with improved annotations and assertions
- **Mockito**: Mocking framework for isolating dependencies
- **Spring Boot Test**: Integration with Spring Boot testing features

## Key Testing Patterns

### 1. Mock Setup with @ExtendWith(MockitoExtension.class)

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserMgntRepository userMgntRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
}
```

### 2. Test Data Setup with @BeforeEach

```java
@BeforeEach
void setUp() {
    // Initialize test data objects
    registrationRequest = new UserRegistrationRequest(...);
    userRole = new UserRole("EMPLOYEE", "Employee Role");
    // Set up common test data
}
```

### 3. Mocking External Dependencies

```java
// Mock repository calls
when(userMgntRepository.existsByEmail(anyString())).thenReturn(false);
when(userRoleRepository.findByRoleName(anyString())).thenReturn(Optional.of(userRole));

// Mock password encoding
when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
```

### 4. Testing Success Scenarios

```java
@Test
void testRegisterUser_Success() {
    // Arrange - set up mocks
    when(userMgntRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRoleRepository.findByRoleName(anyString())).thenReturn(Optional.of(userRole));
    
    // Act - call the method under test
    UserProfileResponse result = userService.registerUser(registrationRequest);
    
    // Assert - verify results and interactions
    assertNotNull(result);
    assertEquals("john.doe@example.com", result.getEmail());
    verify(userMgntRepository).save(any(UserMgnt.class));
}
```

### 5. Testing Exception Scenarios

```java
@Test
void testRegisterUser_EmailAlreadyExists() {
    // Arrange
    when(userMgntRepository.existsByEmail(anyString())).thenReturn(true);
    
    // Act & Assert
    EmailAlreadyExistsException exception = assertThrows(
        EmailAlreadyExistsException.class,
        () -> userService.registerUser(registrationRequest)
    );
    
    assertEquals("Email already exists", exception.getMessage());
    verify(userMgntRepository, never()).save(any());
}
```

## Test Coverage Areas

### UserServiceImplTest Coverage

1. **User Registration**
   - ✅ Successful registration
   - ✅ Email already exists
   - ✅ Role not found
   - ✅ Microservice notification failure handling

2. **User Authentication**
   - ✅ Successful authentication
   - ✅ User not found
   - ✅ Invalid password

3. **Profile Management**
   - ✅ Get user profile success
   - ✅ Get user profile - user not found
   - ✅ Update profile success
   - ✅ Update profile - user not found
   - ✅ Update profile - role not found
   - ✅ Update profile without password change

4. **User Operations**
   - ✅ Get all employees
   - ✅ Get all employees - empty list
   - ✅ Logout user success
   - ✅ Logout user - Redis exception

### MicroserviceIntegrationServiceImplTest Coverage

1. **Employee Expense Service Integration**
   - ✅ Successful notification
   - ✅ Service failure handling
   - ✅ Request mapping verification

2. **Finance Admin Service Integration**
   - ✅ Successful notification for finance admin
   - ✅ Skip notification for regular employee
   - ✅ Service failure handling
   - ✅ Request mapping verification

## Best Practices Implemented

### 1. Isolation
- Each test is independent and doesn't rely on other tests
- All external dependencies are mocked
- Tests can run in any order

### 2. Comprehensive Coverage
- Test both success and failure scenarios
- Test edge cases and boundary conditions
- Verify exception handling

### 3. Clear Test Structure
- **Arrange**: Set up test data and mocks
- **Act**: Execute the method under test
- **Assert**: Verify results and interactions

### 4. Meaningful Assertions
```java
// Verify return values
assertNotNull(result);
assertEquals("expected@email.com", result.getEmail());

// Verify method interactions
verify(userMgntRepository).save(any(UserMgnt.class));
verify(passwordEncoder, never()).encode(anyString());
```

### 5. Proper Exception Testing
```java
InvalidCredentialsException exception = assertThrows(
    InvalidCredentialsException.class,
    () -> userService.authenticateUser(loginRequest)
);
assertEquals("Invalid credentials", exception.getMessage());
```

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceImplTest
```

### Run Service Layer Tests Only
```bash
mvn test -Dtest="*ServiceImplTest"
```

### Generate Test Report
```bash
mvn surefire-report:report
```

## Test Configuration

### Maven Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

### Test Properties
```properties
# src/test/resources/application-test.properties
spring.profiles.active=test
logging.level.com.user.management=DEBUG
```

## Common Testing Patterns

### 1. Testing @Value Fields
```java
@BeforeEach
void setUp() {
    // Use ReflectionTestUtils to set @Value fields
    ReflectionTestUtils.setField(userService, "registrationNotificationFailed", 
            "Failed to notify microservices for user: {}");
}
```

### 2. Testing Void Methods
```java
@Test
void testLogoutUser_Success() {
    doNothing().when(jwtUtil).removeTokenFromRedis(anyString());
    
    assertDoesNotThrow(() -> userService.logoutUser("test@example.com"));
    
    verify(jwtUtil).removeTokenFromRedis("test@example.com");
}
```

### 3. Testing Method Arguments
```java
verify(employeeExpenseClient).registerEmployee(argThat(request -> 
    request.getUserId().equals(employeeUser.getUserId()) &&
    request.getUserEmail().equals(employeeUser.getEmail())
), eq("Bearer " + testToken));
```

## Benefits of This Testing Approach

1. **Fast Execution**: No database or network calls
2. **Reliable**: Tests don't depend on external systems
3. **Comprehensive**: Covers all code paths and edge cases
4. **Maintainable**: Clear structure and meaningful test names
5. **Debugging**: Easy to identify issues when tests fail

## Continuous Integration

These tests are designed to run in CI/CD pipelines:
- No external dependencies required
- Fast execution (< 5 seconds)
- Deterministic results
- Clear failure messages

## Next Steps

1. Add integration tests for end-to-end scenarios
2. Add performance tests for critical paths
3. Consider adding contract tests for microservice interactions
4. Implement test coverage reporting with JaCoCo