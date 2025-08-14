package com.user.management.repository;

import com.user.management.entity.UserMgnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserMgnt entity
 * Provides database operations for user management
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Repository
public interface UserMgntRepository extends JpaRepository<UserMgnt, Long> {

    /**
     * Find user by email address
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM UserMgnt u JOIN FETCH u.userRole WHERE u.email = :email")
    Optional<UserMgnt> findByEmail(@Param("email") String email);

    /**
     * Find active user by email address
     * 
     * @param email the email address to search for
     * @return Optional containing the active user if found
     */
    @Query("SELECT u FROM UserMgnt u JOIN FETCH u.userRole WHERE u.email = :email AND u.isActive = true")
    Optional<UserMgnt> findByEmailAndIsActiveTrue(@Param("email") String email);

    /**
     * Check if email already exists
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    @Query("SELECT COUNT(u) > 0 FROM UserMgnt u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Find all users by role name
     * 
     * @param roleName the role name to search for
     * @return list of users with the specified role
     */
    @Query("SELECT u FROM UserMgnt u JOIN FETCH u.userRole ur WHERE ur.roleName = :roleName AND u.isActive = true")
    List<UserMgnt> findByUserRoleRoleNameAndIsActiveTrue(@Param("roleName") String roleName);

    /**
     * Find all users by department
     * 
     * @param department the department to search for
     * @return list of users in the specified department
     */
    @Query("SELECT u FROM UserMgnt u JOIN FETCH u.userRole WHERE u.department = :department AND u.isActive = true")
    List<UserMgnt> findByDepartmentAndIsActiveTrue(@Param("department") String department);

    /**
     * Find user by ID with role information
     * 
     * @param userId the user ID to search for
     * @return Optional containing the user with role information if found
     */
    @Query("SELECT u FROM UserMgnt u JOIN FETCH u.userRole WHERE u.userId = :userId")
    Optional<UserMgnt> findByIdWithRole(@Param("userId") Long userId);

    /**
     * Find all active users ordered by creation date
     * 
     * @return list of active users
     */
    @Query("SELECT u FROM UserMgnt u JOIN FETCH u.userRole WHERE u.isActive = true ORDER BY u.createdDate DESC")
    List<UserMgnt> findByIsActiveTrueOrderByCreatedDateDesc();
}