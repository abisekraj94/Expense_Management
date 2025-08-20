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
public class FinanceAdmin {

    private Long adminId;

    private String username;

    private String adminName;

    private String email;

    private String role;

    private Boolean isActive;
}