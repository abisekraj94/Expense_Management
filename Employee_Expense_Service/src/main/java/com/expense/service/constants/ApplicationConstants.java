package com.expense.service.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application constants for Employee Expense Service
 * Contains only the constants that are actively used in the codebase
 */
@Component
public class ApplicationConstants {

    // Expense Status Constants
    @Value("${app.expense.status.requested}")
    public String STATUS_REQUESTED;

    // Currency Constants
    @Value("${app.currency.base}")
    public String BASE_CURRENCY;

    // Cache Constants
    @Value("${app.cache.currency.prefix}")
    public String CURRENCY_CACHE_PREFIX;
    
    @Value("${app.cache.currency.ttl}")
    public long CURRENCY_CACHE_TTL;

    // Error Messages
    @Value("${app.message.expense.not.found}")
    public String EXPENSE_NOT_FOUND;
    
    @Value("${app.message.category.not.found}")
    public String CATEGORY_NOT_FOUND;
    
    @Value("${app.message.unauthorized.access}")
    public String UNAUTHORIZED_ACCESS;
    
    @Value("${app.message.currency.conversion.failed}")
    public String CURRENCY_CONVERSION_FAILED;

    // Success Messages
    @Value("${app.message.expense.created.success}")
    public String EXPENSE_CREATED_SUCCESS;
    
    @Value("${app.message.expense.updated.success}")
    public String EXPENSE_UPDATED_SUCCESS;
    
    @Value("${app.message.expense.deleted.success}")
    public String EXPENSE_DELETED_SUCCESS;
    
    @Value("${app.message.category.created.success}")
    public String CATEGORY_CREATED_SUCCESS;
    
    @Value("${app.message.expenses.retrieved.success}")
    public String EXPENSES_RETRIEVED_SUCCESS;

    // Business Rule Messages
    @Value("${app.message.invalid.employee.id}")
    public String INVALID_EMPLOYEE_ID;
    
    @Value("${app.message.can.only.update.requested}")
    public String CAN_ONLY_UPDATE_REQUESTED;
    
    @Value("${app.message.can.only.delete.requested}")
    public String CAN_ONLY_DELETE_REQUESTED;
    
    @Value("${app.message.user.not.found}")
    public String USER_NOT_FOUND;

    // External Service Messages
    @Value("${app.message.external.endpoint.not.found}")
    public String EXTERNAL_ENDPOINT_NOT_FOUND;
    
    @Value("${app.message.external.auth.failed}")
    public String EXTERNAL_AUTH_FAILED;
    
    @Value("${app.message.external.rate.limit}")
    public String EXTERNAL_RATE_LIMIT;
    
    @Value("${app.message.external.internal.error}")
    public String EXTERNAL_INTERNAL_ERROR;
    
    @Value("${app.message.external.unavailable}")
    public String EXTERNAL_UNAVAILABLE;
    
    @Value("${app.message.external.error}")
    public String EXTERNAL_ERROR;

    // Currency Service Messages
    @Value("${app.message.empty.currency.response}")
    public String EMPTY_CURRENCY_RESPONSE;
    
    @Value("${app.message.currency.api.error}")
    public String CURRENCY_API_ERROR;
    
    @Value("${app.message.currency.rate.not.found}")
    public String CURRENCY_RATE_NOT_FOUND;
    
    @Value("${app.message.invalid.exchange.rate}")
    public String INVALID_EXCHANGE_RATE;
    
    @Value("${app.message.currency.timeout}")
    public String CURRENCY_TIMEOUT;
    
    @Value("${app.message.currency.unavailable}")
    public String CURRENCY_UNAVAILABLE;

    // JSON Keys
    @Value("${app.json.key.error}")
    public String JSON_KEY_ERROR;
    
    @Value("${app.json.key.rates}")
    public String JSON_KEY_RATES;

    // Separators
    @Value("${app.url.separator}")
    public String URL_SEPARATOR;
    
    @Value("${app.cache.key.separator}")
    public String CACHE_KEY_SEPARATOR;
}