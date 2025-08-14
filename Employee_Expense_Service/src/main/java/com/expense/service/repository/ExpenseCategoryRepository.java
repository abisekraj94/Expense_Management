package com.expense.service.repository;

import com.expense.service.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExpenseCategory entity
 * Provides database operations for expense categories
 */
@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    /**
     * Find active expense category by ID
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.id = :id AND ec.isActive = true")
    Optional<ExpenseCategory> findByIdAndIsActiveTrue(Long id) throws DataAccessException;

    /**
     * Find all active expense categories
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.isActive = true")
    List<ExpenseCategory> findAllActiveCategories() throws DataAccessException;

    /**
     * Find expense category by category name
     * @throws DataAccessException if database access fails
     */
    Optional<ExpenseCategory> findByCategoryAndIsActiveTrue(String category) throws DataAccessException;
}