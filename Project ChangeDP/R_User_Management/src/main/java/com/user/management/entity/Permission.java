package com.user.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing Permission table
 * Contains endpoint permissions for role-based access control
 * 
 * @author User Management Team
 * @version 1.0
 */
@Entity
@Table(name = "permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    /**
     * Primary key for permission
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Endpoint paths that this permission grants access to
     */
    @Column(name = "end_points", nullable = false, columnDefinition = "TEXT")
    private String endPoints;

    /**
     * One-to-many relationship with role permission mappings
     */
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolePermissionMap> rolePermissionMaps;
}