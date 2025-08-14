package com.finance.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Data Transfer Object for login requests
 * Contains admin credentials for authentication
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    /**
     * Admin username
     */
    @NotBlank(message = "${validation.username.required}")
    private String username;

    /**
     * Admin password
     */
    @NotBlank(message = "${validation.password.required}")
    private String password;
}