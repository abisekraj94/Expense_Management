package com.expense.service.exception;

import com.expense.service.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

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
    public static class ExpenseNotFoundException extends RuntimeException {
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
    public static class UnauthorizedAccessException extends RuntimeException {
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
    public static class CurrencyConversionException extends RuntimeException {
        public CurrencyConversionException(String message) {
            super(message);
        }
        public CurrencyConversionException(String message, Throwable cause) {
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
    public ResponseEntity<ApiResponseDto<Object>> handleExpenseNotFoundException(ExpenseNotFoundException ex) {
        log.error("Expense not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.error(ex.getMessage()));
    }

    /**
     * Handles UnauthorizedAccessException and returns HTTP 403
     * Triggered when user attempts unauthorized operations
     * 
     * @param ex UnauthorizedAccessException containing error details
     * @return ResponseEntity with error message and FORBIDDEN status
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDto.error(ex.getMessage()));
    }

    /**
     * Handles CurrencyConversionException and returns HTTP 503
     * Triggered when external currency API fails or returns invalid data
     * 
     * @param ex CurrencyConversionException containing error details
     * @return ResponseEntity with error message and SERVICE_UNAVAILABLE status
     */
    @ExceptionHandler(CurrencyConversionException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleCurrencyConversionException(CurrencyConversionException ex) {
        log.error("Currency conversion failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.error(ex.getMessage()));
    }

    /**
     * Handles validation exceptions and returns HTTP 400
     * Triggered when request validation fails (e.g., @Valid annotations)
     * 
     * @param ex MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity with field-specific error messages and BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, "Validation failed", errors));
    }

    /**
     * Handles DataAccessException and returns HTTP 500
     * Triggered when database operations fail (connection issues, query errors)
     * 
     * @param ex DataAccessException containing database error details
     * @return ResponseEntity with generic error message and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleDataAccessException(DataAccessException ex) {
        log.error("Database access error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error("Database operation failed"));
    }

    /**
     * Handles DataIntegrityViolationException and returns HTTP 409
     * Triggered when database constraints are violated (unique, foreign key, etc.)
     * 
     * @param ex DataIntegrityViolationException containing constraint violation details
     * @return ResponseEntity with error message and CONFLICT status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.error("Data integrity constraint violated"));
    }



    /**
     * Handles RestClientException and returns HTTP 503
     * Triggered when external API calls fail (currency conversion, etc.)
     * 
     * @param ex RestClientException containing external service error details
     * @return ResponseEntity with error message and SERVICE_UNAVAILABLE status
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleRestClientException(RestClientException ex) {
        log.error("External service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.error("External service unavailable"));
    }

    /**
     * Handles MethodArgumentTypeMismatchException and returns HTTP 400
     * Triggered when request parameters cannot be converted to expected types
     * 
     * @param ex MethodArgumentTypeMismatchException containing type conversion error details
     * @return ResponseEntity with error message and BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Invalid argument type: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error("Invalid parameter type"));
    }

    /**
     * Handles all unhandled exceptions and returns HTTP 500
     * Fallback handler for any exceptions not caught by specific handlers
     * 
     * @param ex Exception containing error details
     * @return ResponseEntity with generic error message and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error("An unexpected error occurred"));
    }
}