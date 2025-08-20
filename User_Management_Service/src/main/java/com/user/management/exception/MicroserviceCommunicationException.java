package com.user.management.exception;

public class MicroserviceCommunicationException extends RuntimeException {
    public MicroserviceCommunicationException(String message) {
        super(message);
    }
    
    public MicroserviceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}