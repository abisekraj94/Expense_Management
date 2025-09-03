package com.user.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing Department table
 * Contains department information and related roles
 * 
 * @author User Management Team
 * @version 1.0
 */
@Entity
@Table(name = "department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    /**
     * Primary key for department
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Department name - cannot be null
     */
    @Column(name = "dep_name", nullable = false)
    private String depName;

    /**
     * One-to-many relationship with roles
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Role> roles;
}