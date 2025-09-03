package com.user.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing Employee_Role_mapping table
 * Maps employees to their roles
 * 
 * @author User Management Team
 * @version 1.0
 */
@Entity
@Table(name = "employee_role_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRoleMapping {

    /**
     * Primary key for employee role mapping
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-one relationship with employee
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", referencedColumnName = "id")
    private Employee employee;

    /**
     * Many-to-one relationship with role
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
}