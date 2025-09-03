package com.expense.management.service.impl;

import com.expense.management.client.UserManagementClient;
import com.expense.management.config.UserManagementConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for integrating with User Management microservice
 * Provides abstraction layer for user management operations
 * 
 * @author System
 * @version 1.0.0
 */
@Service
@Slf4j
public class UserManagementService {

    private final UserManagementClient userManagementClient;
    private final UserManagementConfig userManagementConfig;

    /**
     * Constructor for UserManagementService
     * 
     * @param userManagementClient Feign client for user management
     * @param userManagementConfig configuration properties
     */
    public UserManagementService(UserManagementClient userManagementClient, 
                                UserManagementConfig userManagementConfig) {
        this.userManagementClient = userManagementClient;
        this.userManagementConfig = userManagementConfig;
    }

    /**
     * Validates user authentication with User Management service
     * 
     * @return user details or fallback response
     */
    public Object validateUserAuthentication() {
        try {
            log.info("Calling User Management service at: {}", userManagementConfig.getLoginUrl());
            Object response = userManagementClient.getUserDetails();
            log.info("Successfully received response from User Management service");
            return response;
        } catch (Exception e) {
            log.error("Error calling User Management service: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Checks if User Management service is available
     * 
     * @return true if service is available, false otherwise
     */
    public boolean isUserManagementServiceAvailable() {
        try {
            Object response = userManagementClient.getUserDetails();
            return response != null;
        } catch (Exception e) {
            log.warn("User Management service is not available: {}", e.getMessage());
            return false;
        }
    }
}