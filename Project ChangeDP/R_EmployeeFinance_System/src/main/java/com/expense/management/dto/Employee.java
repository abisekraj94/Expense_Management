package com.expense.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for employee information
 * Contains basic employee details for API responses
 * 
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    /**
     * Employee ID
     */
    private Long id;

    /**
     * Employee's first name
     */
    private String firstName;

    /**
     * Employee's last name
     */
    private String lastName;

    /**
     * Employee's email address
     */
    private String emailId;

    /**
     * Employee's mobile number
     */
    private String mobileNo;

    /**
     * Employee's full name (computed property)
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}