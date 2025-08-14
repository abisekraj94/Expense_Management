package com.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response
 * Contains JWT token and user information after successful authentication
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;

    private String tokenType = "Bearer";

    private Long expiresIn;

    private UserProfileResponse userProfile;

    public AuthenticationResponse(String accessToken, Long expiresIn, UserProfileResponse userProfile) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userProfile = userProfile;
    }
}