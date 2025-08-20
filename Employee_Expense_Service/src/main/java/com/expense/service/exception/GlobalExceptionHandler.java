package com.expense.service.exception;

import com.expense.service.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Global exception handler for the application
 * Provides centralized exception handling with structured error responses
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Custom exception for expense not found scenarios
     */
    public static class ExpenseNotFoundException extends Exception {
        public ExpenseNotFoundException(String message) {
            super(message);
        }
        public ExpenseNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for unauthorized access scenarios
     */
    public static class UnauthorizedAccessException extends Exception {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
        public UnauthorizedAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for currency conversion failures
     */
    public static class CurrencyConversionException extends Exception {
        public CurrencyConversionException(String message) {
            super(message);
        }
        public CurrencyConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for expense category not found scenarios
     */
    public static class ExpenseCategoryNotFoundException extends Exception {
        public ExpenseCategoryNotFoundException(String message) {
            super(message);
        }
        public ExpenseCategoryNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for invalid expense status scenarios
     */
    public static class InvalidExpenseStatusException extends Exception {
        public InvalidExpenseStatusException(String message) {
            super(message);
        }
        public InvalidExpenseStatusException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for business rule violations
     */
    public static class BusinessRuleViolationException extends Exception {
        public BusinessRuleViolationException(String message) {
            super(message);
        }
        public BusinessRuleViolationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for external service failures
     */
    public static class ExternalServiceException extends Exception {
        public ExternalServiceException(String message) {
            super(message);
        }
        public ExternalServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for cache operation failures
     */
    public static class CacheOperationException extends Exception {
        public CacheOperationException(String message) {
            super(message);
        }
        public CacheOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for database operation failures
     */
    public static class DatabaseOperationException extends Exception {
        public DatabaseOperationException(String message) {
            super(message);
        }
        public DatabaseOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for user service failures
     */
    public static class UserServiceException extends Exception {
        public UserServiceException(String message) {
            super(message);
        }
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for category service failures
     */
    public static class CategoryServiceException extends Exception {
        public CategoryServiceException(String message) {
            super(message);
        }
        public CategoryServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Handles ExpenseNotFoundException and returns HTTP 404
     * Triggered when requested expense or category is not found
     * 
     * @param ex ExpenseNotFoundException containing error details
     * @return ResponseEntity with error message and NOT_FOUND status
     */
    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpenseNotFoundException(ExpenseNotFoundException ex) {
        log.error("Expense not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles UnauthorizedAccessException and returns HTTP 403
     * Triggered when user attempts unauthorized operations
     * 
     * @param ex UnauthorizedAccessException containing error details
     * @return ResponseEntity with error message and FORBIDDEN status
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles CurrencyConversionException and returns HTTP 503
     * Triggered when external currency API fails or returns invalid data
     * 
     * @param ex CurrencyConversionException containing error details
     * @return ResponseEntity with error message and SERVICE_UNAVAILABLE status
     */
    @ExceptionHandler(CurrencyConversionException.class)
    public ResponseEntity<ApiResponse<Object>> handleCurrencyConversionException(CurrencyConversionException ex) {
        log.error("Currency conversion failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles ExpenseCategoryNotFoundException and returns HTTP 404
     * Triggered when requested expense category is not found
     * 
     * @param ex ExpenseCategoryNotFoundException containing error details
     * @return ResponseEntity with error message and NOT_FOUND status
     */
    @ExceptionHandler(ExpenseCategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpenseCategoryNotFoundException(ExpenseCategoryNotFoundException ex) {
        log.error("Expense category not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles InvalidExpenseStatusException and returns HTTP 400
     * Triggered when expense status operation is invalid
     * 
     * @param ex InvalidExpenseStatusException containing error details
     * @return ResponseEntity with error message and BAD_REQUEST status
     */
    @ExceptionHandler(InvalidExpenseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidExpenseStatusException(InvalidExpenseStatusException ex) {
        log.error("Invalid expense status operation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles validation exceptions and returns HTTP 400
     * Triggered when request validation fails (e.g., @Valid annotations)
     * 
     * @param ex MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity with field-specific error messages and BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", errors));
    }

    /**
     * Handles DataAccessException and returns HTTP 500
     * Triggered when database operations fail (connection issues, query errors)
     * 
     * @param ex DataAccessException containing database error details
     * @return ResponseEntity with generic error message and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex) {
        log.error("Database access error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database operation failed"));
    }

    /**
     * Handles DataIntegrityViolationException and returns HTTP 409
     * Triggered when database constraints are violated (unique, foreign key, etc.)
     * 
     * @param ex DataIntegrityViolationException containing constraint violation details
     * @return ResponseEntity with error message and CONFLICT status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Data integrity constraint violated"));
    }



    /**
     * Handles RestClientException and returns HTTP 503
     * Triggered when external API calls fail (currency conversion, etc.)
     * 
     * @param ex RestClientException containing external service error details
     * @return ResponseEntity with error message and SERVICE_UNAVAILABLE status
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponse<Object>> handleRestClientException(RestClientException ex) {
        log.error("External service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("External service unavailable"));
    }

    /**
     * Handles MethodArgumentTypeMismatchException and returns HTTP 400
     * Triggered when request parameters cannot be converted to expected types
     * 
     * @param ex MethodArgumentTypeMismatchException containing type conversion error details
     * @return ResponseEntity with error message and BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Invalid argument type: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid parameter type"));
    }

    /**
     * Handles BusinessRuleViolationException and returns HTTP 422
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        log.error("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles ExternalServiceException and returns HTTP 502
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleExternalServiceException(ExternalServiceException ex) {
        log.error("External service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error("External service temporarily unavailable"));
    }

    /**
     * Handles CacheOperationException and returns HTTP 503
     */
    @ExceptionHandler(CacheOperationException.class)
    public ResponseEntity<ApiResponse<Object>> handleCacheOperationException(CacheOperationException ex) {
        log.error("Cache operation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Cache service temporarily unavailable"));
    }

    /**
     * Handles Redis connection failures and returns HTTP 503
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleRedisConnectionException(RedisConnectionFailureException ex) {
        log.error("Redis connection failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Cache service unavailable"));
    }

    /**
     * Handles constraint violations and returns HTTP 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid input data"));
    }

    /**
     * Handles HTTP message not readable exceptions and returns HTTP 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Invalid JSON format: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid JSON format"));
    }

    /**
     * Handles resource access exceptions and returns HTTP 503
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAccessException(ResourceAccessException ex) {
        log.error("Resource access error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("External service timeout"));
    }

    /**
     * Handles transaction exceptions and returns HTTP 500
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ApiResponse<Object>> handleTransactionException(TransactionException ex) {
        log.error("Transaction failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Transaction failed"));
    }

    /**
     * Handles connection exceptions and returns HTTP 503
     */
    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ApiResponse<Object>> handleConnectException(ConnectException ex) {
        log.error("Connection failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Service connection failed"));
    }

    /**
     * Handles timeout exceptions and returns HTTP 408
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<Object>> handleTimeoutException(TimeoutException ex) {
        log.error("Operation timeout: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ApiResponse.error("Request timeout"));
    }

    /**
     * Handles resource not found exceptions and returns HTTP 404
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource not found"));
    }

    /**
     * Handles database operation exceptions and returns HTTP 500
     */
    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDatabaseOperationException(DatabaseOperationException ex) {
        log.error("Database operation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database operation failed"));
    }

    /**
     * Handles user service exceptions and returns HTTP 404
     */
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserServiceException(UserServiceException ex) {
        log.error("User service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles category service exceptions and returns HTTP 500
     */
    @ExceptionHandler(CategoryServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryServiceException(CategoryServiceException ex) {
        log.error("Category service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles all unhandled exceptions and returns HTTP 500
     * Fallback handler for any exceptions not caught by specific handlers
     * 
     * @param ex Exception containing error details
     * @return ResponseEntity with generic error message and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}