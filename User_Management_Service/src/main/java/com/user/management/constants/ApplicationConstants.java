package com.user.management.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application constants for User Management Service
 * Contains only the values that are actually used in the application
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Component
public class ApplicationConstants {

    // User Roles - Used in DataInitializer
    @Value("${user.role.employee}")
    public String ROLE_EMPLOYEE;
    
    @Value("${user.role.finance.admin}")
    public String ROLE_FINANCE_ADMIN;

    // JWT Constants - Used in JwtUtil and JwtAuthenticationFilter
    @Value("${jwt.token.prefix}")
    public String JWT_TOKEN_PREFIX;
    
    @Value("${jwt.header.name}")
    public String JWT_HEADER_NAME;
    
    @Value("${jwt.redis.prefix}")
    public String JWT_REDIS_PREFIX;
    
    @Value("${jwt.token.validity}")
    public long JWT_TOKEN_VALIDITY;

    // Error Messages - Used in Controllers and GlobalExceptionHandler
    @Value("${error.user.not.found}")
    public String USER_NOT_FOUND;
    
    @Value("${error.invalid.credentials}")
    public String INVALID_CREDENTIALS;
    
    @Value("${error.email.already.exists}")
    public String EMAIL_ALREADY_EXISTS;
    
    @Value("${error.access.denied}")
    public String ACCESS_DENIED;

    // Success Messages - Used in Controllers
    @Value("${success.user.registered}")
    public String USER_REGISTERED_SUCCESSFULLY;
    
    @Value("${success.login}")
    public String LOGIN_SUCCESSFUL;
    
    @Value("${success.profile.updated}")
    public String PROFILE_UPDATED_SUCCESSFULLY;

    // HTTP Status Messages - Used in GlobalExceptionHandler
    @Value("${status.validation.failed}")
    public String VALIDATION_FAILED;

    // Application Messages - Used in Controllers
    @Value("${app.message.logout.success}")
    public String LOGOUT_SUCCESS;
    
    @Value("${app.message.service.running}")
    public String SERVICE_RUNNING;
    
    @Value("${app.message.service.status}")
    public String SERVICE_STATUS;
    
    @Value("${app.message.employees.retrieved}")
    public String EMPLOYEES_RETRIEVED;
    
    @Value("${app.message.profile.retrieved}")
    public String PROFILE_RETRIEVED;
    
    // Entity Descriptions - Used in DataInitializer
    @Value("${entity.role.employee.description}")
    public String EMPLOYEE_ROLE_DESCRIPTION;
    
    @Value("${entity.role.finance.admin.description}")
    public String FINANCE_ADMIN_ROLE_DESCRIPTION;
}