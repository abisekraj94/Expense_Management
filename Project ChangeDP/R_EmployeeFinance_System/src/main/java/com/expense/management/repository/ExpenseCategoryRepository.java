package com.expense.management.repository;

import com.expense.management.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExpenseCategory entity
 * Provides database operations for expense category management
 * 
 * @author System
 * @version 1.0.0
 */
@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {

    /**
     * Finds active expense category by ID
     * 
     * @param id category ID
     * @return Optional containing active expense category if found
     */
    Optional<ExpenseCategory> findByIdAndIsActiveTrue(Integer id);
}