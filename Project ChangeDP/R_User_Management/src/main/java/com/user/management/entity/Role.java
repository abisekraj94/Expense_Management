package com.user.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing Roles table
 * Contains role information with department association
 * 
 * @author User Management Team
 * @version 1.0
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * Primary key for role
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name - cannot be null
     */
    @Column(name = "role_name", nullable = false)
    private String roleName;

    /**
     * Many-to-one relationship with department
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dep_id", referencedColumnName = "id")
    private Department department;

    /**
     * One-to-many relationship with employee role mappings
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeRoleMapping> employeeRoleMappings;

    /**
     * One-to-many relationship with role permission mappings
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolePermissionMap> rolePermissionMaps;
}