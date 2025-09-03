package com.expense.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for User Management service
 * Centralizes all user management service related configurations
 * 
 * @author System
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "user.management")
@Data
public class UserManagementConfig {

    /**
     * Base URL for User Management service
     */
    private String baseUrl = "http://localhost:8081";

    /**
     * Login endpoint path
     */
    private String loginEndpoint = "/api/auth/login";

    /**
     * Connection timeout in milliseconds
     */
    private int timeout = 5000;

    /**
     * Whether to enable fallback for user management calls
     */
    private boolean fallbackEnabled = true;

    /**
     * Gets the complete login URL
     * 
     * @return complete login URL
     */
    public String getLoginUrl() {
        return baseUrl + loginEndpoint;
    }
}