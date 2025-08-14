package com.user.management.repository;

import com.user.management.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserRole entity
 * Provides database operations for user roles
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Find user role by role name
     * 
     * @param roleName the role name to search for
     * @return Optional containing the user role if found
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.roleName = :roleName")
    Optional<UserRole> findByRoleName(@Param("roleName") String roleName);

    /**
     * Check if role exists by role name
     * 
     * @param roleName the role name to check
     * @return true if role exists, false otherwise
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.roleName = :roleName")
    boolean existsByRoleName(@Param("roleName") String roleName);
}