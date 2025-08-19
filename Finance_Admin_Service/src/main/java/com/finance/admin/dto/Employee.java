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

    /**
     * Employee identifier
     */
    private Long employeeId;

    /**
     * Employee name
     */
    private String employeeName;

    /**
     * Employee email
     */
    private String email;

    /**
     * Employee department
     */
    private String department;

    /**
     * Employee designation
     */
    private String designation;
}