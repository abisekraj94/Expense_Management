package com.expense.service.repository;

import com.expense.service.entity.EmployeeExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmployeeExpense entity
 * Provides database operations for employee expenses
 */
@Repository
public interface EmployeeExpenseRepository extends JpaRepository<EmployeeExpense, Long> {

    /**
     * Find active expense by ID
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT ee FROM EmployeeExpense ee WHERE ee.id = :id AND ee.isActive = true")
    Optional<EmployeeExpense> findByIdAndIsActiveTrue(@Param("id") Long id) throws DataAccessException;

    /**
     * Find all active expenses for an employee
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT ee FROM EmployeeExpense ee WHERE ee.employeeId = :employeeId AND ee.isActive = true ORDER BY ee.createdAt DESC")
    List<EmployeeExpense> findByEmployeeIdAndIsActiveTrue(@Param("employeeId") Long employeeId) throws DataAccessException;

    /**
     * Find expenses by employee ID and status
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT ee FROM EmployeeExpense ee WHERE ee.employeeId = :employeeId AND ee.status = :status AND ee.isActive = true ORDER BY ee.createdAt DESC")
    List<EmployeeExpense> findByEmployeeIdAndStatusAndIsActiveTrue(@Param("employeeId") Long employeeId, @Param("status") String status) throws DataAccessException;

    /**
     * Find all active expenses
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT ee FROM EmployeeExpense ee WHERE ee.isActive = true ORDER BY ee.createdAt DESC")
    List<EmployeeExpense> findAllActiveExpenses() throws DataAccessException;

    /**
     * Check if expense belongs to employee and is in requested status
     * @throws DataAccessException if database access fails
     */
    @Query("SELECT COUNT(ee) > 0 FROM EmployeeExpense ee WHERE ee.id = :expenseId AND ee.employeeId = :employeeId AND ee.status = 'Requested' AND ee.isActive = true")
    boolean existsByIdAndEmployeeIdAndStatusRequested(@Param("expenseId") Long expenseId, @Param("employeeId") Long employeeId) throws DataAccessException;
}