package com.finance.admin.dto;

import lombok.*;

/**
 * Data Transfer Object for Finance Admin entity
 * Used for API responses (excludes sensitive data like password)
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceAdminDto {

    /**
     * Admin identifier
     */
    private Long adminId;

    /**
     * Admin username
     */
    private String username;

    /**
     * Admin full name
     */
    private String adminName;

    /**
     * Admin email
     */
    private String email;

    /**
     * Admin role
     */
    private String role;

    /**
     * Account status
     */
    private Boolean isActive;
}