package com.expense.management.exception;

/**
 * Custom exception for business logic violations
 * Thrown when business rules are violated during processing
 * 
 * @author System
 * @version 1.0.0
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message error message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message error message
     * @param cause root cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}