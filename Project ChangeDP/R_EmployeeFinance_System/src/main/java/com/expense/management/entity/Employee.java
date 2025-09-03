package com.expense.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employee entity representing employee information
 * Maps to the employee table in the database
 * 
 * @author System
 * @version 1.0.0
 */
@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    /**
     * Primary key for employee
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Employee's first name
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Employee's last name
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Employee's email address (unique identifier)
     */
    @Column(name = "email_id", unique = true, nullable = false)
    private String emailId;

    /**
     * Employee's mobile number
     */
    @Column(name = "mobile_no")
    private String mobileNo;

    /**
     * Employee's password for authentication
     */
    @Column(name = "password")
    private String password;
}