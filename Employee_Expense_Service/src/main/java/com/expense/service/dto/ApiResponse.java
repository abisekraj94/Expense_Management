package com.expense.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper
 * Provides consistent response structure across all endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    /**
     * Constructor for ApiResponseDto with automatic timestamp
     * Creates response with current timestamp
     * 
     * @param success Whether the operation was successful
     * @param message Response message
     * @param data Response data payload
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a successful API response
     * Factory method for success responses with data
     * 
     * @param <T> Type of response data
     * @param message Success message
     * @param data Response data payload
     * @return ApiResponseDto with success=true and provided data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates an error API response
     * Factory method for error responses without data
     * 
     * @param <T> Type of response data
     * @param message Error message
     * @return ApiResponseDto with success=false and null data
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}