# User Management Service Integration

This document describes the configuration and integration setup for the User Management microservice.

## Configuration Properties

The following properties have been added to `application.properties`:

```properties
# User Management Service Configuration
user.management.base-url=http://localhost:8081
user.management.login.endpoint=/api/auth/login
user.management.timeout=5000
user.management.fallback-enabled=true
user.management.retry.max-attempts=3
user.management.retry.delay=1000

# Feign Client Configuration for User Management
spring.cloud.openfeign.client.config.user-management.connect-timeout=5000
spring.cloud.openfeign.client.config.user-management.read-timeout=5000
```

## Components Created

### 1. UserManagementClient (Feign Client)
- **Location**: `com.expense.management.client.UserManagementClient`
- **Purpose**: Feign client interface for calling User Management service
- **Endpoint**: Configurable via `user.management.login.endpoint`
- **Fallback**: `UserManagementClientFallback`

### 2. UserManagementClientFallback
- **Location**: `com.expense.management.client.UserManagementClientFallback`
- **Purpose**: Provides fallback responses when User Management service is unavailable
- **Response**: Structured JSON with error details and timestamp

### 3. UserManagementConfig
- **Location**: `com.expense.management.config.UserManagementConfig`
- **Purpose**: Configuration properties class for User Management service
- **Features**: Centralized configuration with utility methods

### 4. UserManagementService
- **Location**: `com.expense.management.service.impl.UserManagementService`
- **Purpose**: Service wrapper for User Management integration
- **Features**: Error handling, logging, service availability checks

### 5. UserManagementTestController
- **Location**: `com.expense.management.controller.UserManagementTestController`
- **Purpose**: Test endpoints for verifying User Management integration
- **Endpoints**:
  - `GET /api/v1/test/user-management/test-connection`
  - `GET /api/v1/test/user-management/health-check`

## Usage Examples

### Testing the Integration

1. **Health Check**:
   ```bash
   curl http://localhost:8080/api/v1/test/user-management/health-check
   ```

2. **Connection Test**:
   ```bash
   curl http://localhost:8080/api/v1/test/user-management/test-connection
   ```

### Service Integration in Code

```java
@Autowired
private UserManagementService userManagementService;

// Check if service is available
boolean isAvailable = userManagementService.isUserManagementServiceAvailable();

// Call user authentication
Object userDetails = userManagementService.validateUserAuthentication();
```

## Configuration Customization

You can customize the User Management service configuration by modifying the properties:

```properties
# Change the base URL
user.management.base-url=http://your-user-service:8081

# Change the login endpoint
user.management.login.endpoint=/api/v1/auth/login

# Adjust timeout settings
user.management.timeout=10000
```

## Error Handling

The integration includes comprehensive error handling:

1. **Circuit Breaker**: Enabled via Feign configuration
2. **Fallback Responses**: Structured error responses when service is unavailable
3. **Retry Mechanism**: Configurable retry attempts with delay
4. **Logging**: Detailed logging for debugging and monitoring

## Monitoring

Monitor the User Management service integration using:

1. **Health Check Endpoint**: Regular health checks
2. **Application Logs**: Detailed logging of service calls
3. **Fallback Responses**: Track when fallbacks are triggered

## Security Considerations

- Configure appropriate timeouts to prevent hanging requests
- Implement proper authentication/authorization for inter-service calls
- Use HTTPS in production environments
- Monitor and log all inter-service communications