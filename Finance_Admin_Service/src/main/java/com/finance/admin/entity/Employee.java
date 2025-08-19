package com.finance.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Employee entity representing employee information
 * Contains basic employee details for expense management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Entity
@Table(name = "employee_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    /**
     * Primary key for employee
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    /**
     * Employee's full name
     */
    @NotBlank(message = "${validation.name.required}")
    @Size(max = 100, message = "${validation.name.max.length}")
    @Column(name = "employee_name", nullable = false, length = 100)
    private String employeeName;

    /**
     * Employee's email address
     */
    @NotBlank(message = "${validation.email.required}")
    @Email(message = "${validation.email.invalid}")
    @Size(max = 150, message = "${validation.email.max.length}")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Employee's department
     */
    @Size(max = 50, message = "${validation.department.max.length}")
    @Column(name = "department", length = 50)
    private String department;

    /**
     * Employee's designation
     */
    @Size(max = 50, message = "${validation.designation.max.length}")
    @Column(name = "designation", length = 50)
    private String designation;

    /**
     * List of expenses submitted by this employee
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses;
}