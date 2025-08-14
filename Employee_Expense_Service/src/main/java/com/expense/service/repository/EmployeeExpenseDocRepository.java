package com.expense.service.repository;

import com.expense.service.entity.EmployeeExpenseDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Repository interface for EmployeeExpenseDoc entity
 * Provides database operations for expense documents
 */
@Repository
public interface EmployeeExpenseDocRepository extends JpaRepository<EmployeeExpenseDoc, Long> {

    /**
     * Find all active documents for an expense
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT eed FROM EmployeeExpenseDoc eed WHERE eed.employeeExpense.id = :expenseId AND eed.isActive = true")
    List<EmployeeExpenseDoc> findByEmployeeExpenseIdAndIsActiveTrue(@Param("expenseId") Long expenseId) throws DataAccessException;

    /**
     * Delete all documents for an expense (soft delete)
     * @throws DataAccessException if database access fails
     */
    @Modifying
    @Query("UPDATE EmployeeExpenseDoc eed SET eed.isActive = false WHERE eed.employeeExpense.id = :expenseId")
    void softDeleteByEmployeeExpenseId(@Param("expenseId") Long expenseId) throws DataAccessException;
}