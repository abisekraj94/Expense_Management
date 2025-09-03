package com.user.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing Employee table
 * Contains employee information and authentication details
 * 
 * @author User Management Team
 * @version 1.0
 */
@Entity
@Table(name = "Employee")
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
     * Employee first name
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Employee last name
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Employee email - unique and cannot be null
     */
    @Column(name = "email_id", unique = true, nullable = false)
    private String emailId;

    /**
     * Employee mobile number
     */
    @Column(name = "mobile_no")
    private String mobileNo;

    /**
     * Employee password for authentication
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * One-to-many relationship with employee role mappings
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EmployeeRoleMapping> employeeRoleMappings;
}