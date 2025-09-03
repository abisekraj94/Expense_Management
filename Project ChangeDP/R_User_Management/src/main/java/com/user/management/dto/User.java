package com.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for user information
 * Contains user details without sensitive information
 * 
 * @author User Management Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * User ID
     */
    private Long id;

    /**
     * User first name
     */
    private String firstName;

    /**
     * User last name
     */
    private String lastName;

    /**
     * User email
     */
    private String email;

    /**
     * User mobile number
     */
    private String mobileNo;

    /**
     * User roles
     */
    private List<String> roles;
}