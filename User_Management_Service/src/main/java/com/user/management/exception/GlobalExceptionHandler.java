package com.user.management.exception;

import com.user.management.constants.ApplicationConstants;
import com.user.management.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the User Management Service
 * Handles all exceptions and provides structured error responses
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ApplicationConstants constants;
    
    public GlobalExceptionHandler(ApplicationConstants constants) {
        this.constants = constants;
    }

    /**
     * Handle business logic exceptions
     * 
     * @param ex the RuntimeException
     * @return structured error response
     */
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("already exists")) {
            status = HttpStatus.CONFLICT;
        } else if (message.contains("Invalid credentials")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        
        log.error("Business exception: {}", message);
        ApiResponse<Object> response = ApiResponse.error(message);
        return new ResponseEntity<>(response, status);
    }

    /**
     * Handle access denied exceptions
     * 
     * @param ex the AccessDeniedException
     * @return structured error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(constants.ACCESS_DENIED);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle validation exceptions
     * 
     * @param ex the MethodArgumentNotValidException
     * @return structured error response with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            constants.VALIDATION_FAILED,
            "Validation failed",
            errors
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    /**
     * Handle all other exceptions
     * 
     * @param ex the Exception
     * @return structured error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}