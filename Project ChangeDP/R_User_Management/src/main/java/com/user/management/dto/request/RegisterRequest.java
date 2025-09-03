package com.user.management.dto.request;

import com.user.management.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for user registration request
 * Contains all required information for user registration
 * 
 * @author User Management Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * User first name
     */
    @NotBlank(message = ApplicationConstants.USER_NAME_REQUIRED)
    private String firstName;

    /**
     * User last name
     */
    @NotBlank(message = ApplicationConstants.USER_NAME_REQUIRED)
    private String lastName;

    /**
     * User email for registration
     */
    @NotBlank(message = ApplicationConstants.USER_EMAIL_REQUIRED)
    @Email(message = ApplicationConstants.USER_EMAIL_INVALID)
    private String email;

    /**
     * User mobile number
     */
    @Pattern(regexp = "^[0-9]{10}$", message = ApplicationConstants.USER_MOBILE_INVALID)
    private String mobileNo;

    /**
     * User password for registration
     */
    @NotBlank(message = ApplicationConstants.USER_PASSWORD_REQUIRED)
    private String password;

    /**
     * Role ID for the user
     */
    private List<Long> roles;
}