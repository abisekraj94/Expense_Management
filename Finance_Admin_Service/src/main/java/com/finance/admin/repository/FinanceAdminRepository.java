package com.finance.admin.repository;

import com.finance.admin.entity.FinanceAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for FinanceAdmin entity
 * Provides database operations for admin management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Repository
public interface FinanceAdminRepository extends JpaRepository<FinanceAdmin, Long> {

    /**
     * Find admin by username
     * 
     * @param username admin username
     * @return optional admin
     */
    Optional<FinanceAdmin> findByUsernameAndIsDeletedFalse(String username);

    /**
     * Find admin by email
     * 
     * @param email admin email
     * @return optional admin
     */
    Optional<FinanceAdmin> findByEmailAndIsDeletedFalse(String email);

    /**
     * Check if username exists
     * 
     * @param username admin username
     * @return true if exists
     */
    boolean existsByUsernameAndIsDeletedFalse(String username);

    /**
     * Check if email exists
     * 
     * @param email admin email
     * @return true if exists
     */
    boolean existsByEmailAndIsDeletedFalse(String email);
}