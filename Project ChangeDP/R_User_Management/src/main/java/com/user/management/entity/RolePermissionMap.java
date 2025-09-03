package com.user.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing Role_Permission_Map table
 * Maps roles to their permissions
 * 
 * @author User Management Team
 * @version 1.0
 */
@Entity
@Table(name = "role_permission_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionMap {

    /**
     * Primary key for role permission mapping
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-one relationship with role
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    /**
     * Many-to-one relationship with permission
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id", referencedColumnName = "id")
    private Permission permission;
}