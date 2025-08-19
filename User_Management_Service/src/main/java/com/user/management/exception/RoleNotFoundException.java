package com.user.management.exception;

/**
 * Exception thrown when a role is not found
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}