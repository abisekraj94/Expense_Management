package com.user.management.service;

import com.user.management.dto.response.AuthResponse;
import com.user.management.dto.request.LoginRequest;
import com.user.management.dto.request.RegisterRequest;
import com.user.management.dto.response.RegisteredResponse;

/**
 * Service interface for authentication operations
 * Defines contract for user registration, login, and logout
 * 
 * @author User Management Team
 * @version 1.0
 */
public interface AuthService {

    /**
     * Register a new user
     * 
     * @param registerRequest the registration request data
     * @return authentication response with token
     */
    RegisteredResponse register(RegisterRequest registerRequest);

    /**
     * Authenticate user login
     * 
     * @param loginRequest the login request data
     * @return authentication response with token
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Logout user and invalidate token
     * 
     * @param userId the user ID to logout
     */
    void logout(Long userId);

    /**
     * Validate if token exists in Redis
     * 
     * @param userId the user ID
     * @param token the JWT token
     * @return true if token is valid and exists in Redis
     */
    boolean validateTokenInRedis(Long userId, String token);
}