package com.user.management.repository;

import com.user.management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity
 * Provides database operations for role management
 * 
 * @author User Management Team
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}