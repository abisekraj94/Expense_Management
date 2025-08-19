package com.expense.service.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application constants for Employee Expense Service
 * Contains all values read from application.properties
 */
@Component
public class ApplicationConstants {

    @Value("${app.expense.status.requested}")
    public String STATUS_REQUESTED;
    
    @Value("${app.expense.status.inprogress}")
    public String STATUS_IN_PROGRESS;
    
    @Value("${app.expense.status.approved}")
    public String STATUS_APPROVED;
    
    @Value("${app.expense.status.rejected}")
    public String STATUS_REJECTED;
    
    @Value("${app.expense.status.reimbursed}")
    public String STATUS_REIMBURSED;

    // Expense Categories
    @Value("${app.expense.category.hardware}")
    public String CATEGORY_HARDWARE;
    
    @Value("${app.expense.category.food}")
    public String CATEGORY_FOOD;
    
    @Value("${app.expense.category.certification}")
    public String CATEGORY_CERTIFICATION;
    
    @Value("${app.expense.category.travel}")
    public String CATEGORY_TRAVEL;
    
    @Value("${app.expense.category.accommodation}")
    public String CATEGORY_ACCOMMODATION;

    // Currency Constants
    @Value("${app.currency.base}")
    public String BASE_CURRENCY = "INR";
    
    @Value("${app.currency.usd}")
    public String USD_CURRENCY;
    
    @Value("${app.currency.eur}")
    public String EUR_CURRENCY;

    // API Endpoints
    @Value("${app.api.version}")
    public String API_VERSION;
    
    @Value("${app.api.employee}")
    public String EMPLOYEE_ENDPOINT;
    
    @Value("${app.api.admin}")
    public String ADMIN_ENDPOINT;

    // Cache Constants
    @Value("${app.cache.currency.prefix}")
    public String CURRENCY_CACHE_PREFIX;
    
    @Value("${app.cache.currency.ttl}")
    public long CURRENCY_CACHE_TTL;

    // JWT Constants
    @Value("${app.jwt.header}")
    public String JWT_HEADER;
    
    @Value("${app.jwt.prefix}")
    public String JWT_PREFIX;
    
    @Value("${jwt.expiration}")
    public long JWT_EXPIRATION;

    // Error Messages
    @Value("${app.message.expense.not.found}")
    public String EXPENSE_NOT_FOUND;
    
    @Value("${app.message.category.not.found}")
    public String CATEGORY_NOT_FOUND;
    
    @Value("${app.message.unauthorized.access}")
    public String UNAUTHORIZED_ACCESS;
    
    @Value("${app.message.invalid.status.transition}")
    public String INVALID_STATUS_TRANSITION;
    
    @Value("${app.message.currency.conversion.failed}")
    public String CURRENCY_CONVERSION_FAILED;

    // Success Messages
    @Value("${app.message.expense.created.success}")
    public String EXPENSE_CREATED_SUCCESS;
    
    @Value("${app.message.expense.updated.success}")
    public String EXPENSE_UPDATED_SUCCESS;
    
    @Value("${app.message.expense.deleted.success}")
    public String EXPENSE_DELETED_SUCCESS;
}