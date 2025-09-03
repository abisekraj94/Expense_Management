package com.expense.management.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for UserManagementClient
 * Provides default responses when User Management service is unavailable
 * 
 * @author System
 * @version 1.0.0
 */
@Component
@Slf4j
public class UserManagementClientFallback implements UserManagementClient {
    
    @Override
    public Object getUserDetails() {
        log.warn("User Management service is unavailable, returning fallback response");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("status", "SERVICE_UNAVAILABLE");
        fallbackResponse.put("message", "User management service is currently unavailable");
        fallbackResponse.put("timestamp", System.currentTimeMillis());
        fallbackResponse.put("fallback", true);
        
        return fallbackResponse;
    }
}