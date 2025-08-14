package com.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user profile response
 * Contains user profile information without sensitive data
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long userId;

    private String name;

    private String email;

    private String department;

    private String role;

    private Boolean isActive;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}