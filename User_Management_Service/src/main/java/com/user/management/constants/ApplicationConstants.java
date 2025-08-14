package com.user.management.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application constants for User Management Service
 * Contains all values loaded from properties file
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Component
public class ApplicationConstants {

    // User Roles
    @Value("${user.role.employee}")
    public String ROLE_EMPLOYEE;
    
    @Value("${user.role.finance.admin}")
    public String ROLE_FINANCE_ADMIN;

    // JWT Constants
    @Value("${jwt.token.prefix}")
    public String JWT_TOKEN_PREFIX;
    
    @Value("${jwt.header.name}")
    public String JWT_HEADER_NAME;
    
    @Value("${jwt.redis.prefix}")
    public String JWT_REDIS_PREFIX;
    
    @Value("${jwt.token.validity}")
    public long JWT_TOKEN_VALIDITY;

    // API Endpoints
    @Value("${api.base.path}")
    public String API_BASE_PATH;
    
    @Value("${api.auth.endpoint}")
    public String AUTH_ENDPOINT;
    
    @Value("${api.user.endpoint}")
    public String USER_ENDPOINT;

    // Error Messages
    @Value("${error.user.not.found}")
    public String USER_NOT_FOUND;
    
    @Value("${error.invalid.credentials}")
    public String INVALID_CREDENTIALS;
    
    @Value("${error.email.already.exists}")
    public String EMAIL_ALREADY_EXISTS;
    
    @Value("${error.invalid.token}")
    public String INVALID_TOKEN;
    
    @Value("${error.access.denied}")
    public String ACCESS_DENIED;

    // Success Messages
    @Value("${success.user.registered}")
    public String USER_REGISTERED_SUCCESSFULLY;
    
    @Value("${success.login}")
    public String LOGIN_SUCCESSFUL;
    
    @Value("${success.profile.updated}")
    public String PROFILE_UPDATED_SUCCESSFULLY;

    // Validation Messages
    @Value("${validation.email.required}")
    public String EMAIL_REQUIRED;
    
    @Value("${validation.password.required}")
    public String PASSWORD_REQUIRED;
    
    @Value("${validation.name.required}")
    public String NAME_REQUIRED;
    
    @Value("${validation.department.required}")
    public String DEPARTMENT_REQUIRED;
    
    @Value("${validation.role.required}")
    public String ROLE_REQUIRED;
    
    @Value("${validation.email.format}")
    public String INVALID_EMAIL_FORMAT;

    // Database Constants
    @Value("${db.table.user.role}")
    public String USER_ROLE_TABLE;
    
    @Value("${db.table.user.mgnt}")
    public String USER_MGNT_TABLE;
    
    @Value("${db.created.by.system}")
    public String CREATED_BY_SYSTEM;
    
    @Value("${db.updated.by.system}")
    public String UPDATED_BY_SYSTEM;

    // HTTP Status Messages
    @Value("${status.success}")
    public String SUCCESS;
    
    @Value("${status.error}")
    public String ERROR;
    
    @Value("${status.validation.failed}")
    public String VALIDATION_FAILED;

    // Extended Validation Messages
    @Value("${validation.name.size}")
    public String NAME_SIZE_MESSAGE;
    
    @Value("${validation.email.size}")
    public String EMAIL_SIZE_MESSAGE;
    
    @Value("${validation.password.size}")
    public String PASSWORD_SIZE_MESSAGE;
    
    @Value("${validation.department.size}")
    public String DEPARTMENT_SIZE_MESSAGE;
    
    @Value("${validation.role.pattern}")
    public String ROLE_PATTERN_MESSAGE;

    // Application Messages
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
    
    @Value("${entity.role.employee.description}")
    public String EMPLOYEE_ROLE_DESCRIPTION;
    
    @Value("${entity.role.finance.admin.description}")
    public String FINANCE_ADMIN_ROLE_DESCRIPTION;
}