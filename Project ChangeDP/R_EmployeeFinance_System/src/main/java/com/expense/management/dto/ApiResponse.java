package com.expense.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper DTO
 * Provides consistent response structure across all API endpoints
 * 
 * @param <T> Type of data being returned
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Response status (success/error)
     */
    private String status;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Response timestamp
     */
    private LocalDateTime timestamp;

    /**
     * Constructor for success response
     * 
     * @param message success message
     * @param data response data
     */
    public ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor for error response
     * 
     * @param message error message
     */
    public ApiResponse(String message) {
        this.status = "error";
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Static method to create success response
     * 
     * @param message success message
     * @param data response data
     * @param <T> data type
     * @return success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    /**
     * Static method to create error response
     * 
     * @param message error message
     * @param <T> data type
     * @return error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message);
    }
}