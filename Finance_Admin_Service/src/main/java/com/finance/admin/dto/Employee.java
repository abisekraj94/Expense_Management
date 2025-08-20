package com.finance.admin.dto;

import lombok.*;

/**
 * Data Transfer Object for Employee entity
 * Used for API requests and responses
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    private Long employeeId;

    private String employeeName;

    private String email;

    private String department;

    private String designation;
}