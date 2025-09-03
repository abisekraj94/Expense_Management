package com.user.management.dto.response;

import com.user.management.exception.AuthException;
import com.user.management.exception.UserOperationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO for API responses
 * Provides consistent response structure across all endpoints
 * 
 * @author User Management Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Success status of the operation
     */
    private boolean success;

    /**
     * Error code to identify
     */
    private String errorCode;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Constructor for success response with data
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Constructor for success response without data
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Static method to create success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Static method to create success response without data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    /**
     * Static method to create error response
     */
    public static <T> ApiResponse<T> error(String message, String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(error);
        return response;
    }

    public static <T> ApiResponse<T> error(UserOperationException error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setErrorCode(error.getErrorCode());
        response.setMessage(error.getErrorMessage());
        return response;
    }

    public static <T> ApiResponse<T> error(AuthException error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setErrorCode(error.getErrorCode());
        response.setMessage(error.getErrorMessage());
        return response;
    }
}