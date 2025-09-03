package com.user.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for authentication response
 * Contains JWT token and user information
 * 
 * @author User Management Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT access token
     */
    private String token;

    /**
     * Token type (Bearer)
     */
    private String tokenType = "Bearer";

    /**
     * User ID
     */
    private Long userId;

    /**
     * User email
     */
    private String email;

    /**
     * User full name
     */
    private String fullName;

    /**
     * User roles
     */
    private List<String> roles;

    /**
     * Constructor without tokenType (defaults to Bearer)
     */
    public AuthResponse(String token, Long userId, String email, String fullName, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }
}