package com.expense.management.exception;

/**
 * Custom exception for external API failures
 * Thrown when external API calls fail or return unexpected responses
 * 
 * @author System
 * @version 1.0.0
 */
public class ExternalApiException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message error message
     */
    public ExternalApiException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message error message
     * @param cause root cause
     */
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}