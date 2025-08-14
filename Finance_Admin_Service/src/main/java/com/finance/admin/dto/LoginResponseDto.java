package com.finance.admin.dto;

import lombok.*;

/**
 * Data Transfer Object for login responses
 * Contains JWT token and admin information
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    /**
     * JWT access token
     */
    private String accessToken;

    /**
     * Token type (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Token expiration time in milliseconds
     */
    private Long expiresIn;

    /**
     * Admin information
     */
    private FinanceAdminDto admin;
}