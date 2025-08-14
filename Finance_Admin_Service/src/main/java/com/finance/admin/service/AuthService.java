package com.finance.admin.service;

import com.finance.admin.dto.LoginRequestDto;
import com.finance.admin.dto.LoginResponseDto;

/**
 * Service interface for authentication operations
 * Handles admin login and JWT token management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
public interface AuthService {

    /**
     * Authenticate admin and generate JWT token
     * 
     * @param loginRequest login credentials
     * @return login response with JWT token
     */
    LoginResponseDto authenticateAdmin(LoginRequestDto loginRequest);

    /**
     * Validate JWT token
     * 
     * @param token JWT token
     * @return true if valid
     */
    boolean validateToken(String token);

    /**
     * Extract username from JWT token
     * 
     * @param token JWT token
     * @return username
     */
    String getUsernameFromToken(String token);
}