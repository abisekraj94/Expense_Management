package com.user.management.dto.request;

import com.user.management.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login request
 * Contains email and password for authentication
 * 
 * @author User Management Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * User email for login
     */
    @NotBlank(message = ApplicationConstants.USER_EMAIL_REQUIRED)
    @Email(message = ApplicationConstants.USER_EMAIL_INVALID)
    private String email;

    /**
     * User password for login
     */
    @NotBlank(message = ApplicationConstants.USER_PASSWORD_REQUIRED)
    private String password;
}