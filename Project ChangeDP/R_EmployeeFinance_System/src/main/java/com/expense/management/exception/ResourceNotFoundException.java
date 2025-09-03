package com.expense.management.exception;

/**
 * Custom exception for resource not found scenarios
 * Thrown when requested resource is not found in the database
 * 
 * @author System
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message error message
     * @param cause root cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}