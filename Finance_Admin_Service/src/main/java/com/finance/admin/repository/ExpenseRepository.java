package com.finance.admin.repository;

import com.finance.admin.entity.Expense;
import com.finance.admin.entity.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Expense entity
 * Provides database operations for expense management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find expenses by status with pagination
     * 
     * @param status   expense status
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<Expense> findByStatusAndIsDeletedFalse(ExpenseStatus status, Pageable pageable);

    /**
     * Find pending expenses with pagination
     * 
     * @param pageable pagination information
     * @return page of pending expenses
     */
    Page<Expense> findByStatusAndIsDeletedFalseOrderByCreatedAtAsc(ExpenseStatus status, Pageable pageable);

    /**
     * Find approved expenses by employee and date range
     * 
     * @param employeeId employee ID
     * @param startDate  start date
     * @param endDate    end date
     * @return list of approved expenses
     */
    @Query("SELECT e FROM Expense e " +
           "WHERE e.employee.employeeId = :employeeId " +
           "AND e.status = 'APPROVED' " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "AND e.isDeleted = false " +
           "ORDER BY e.expenseDate DESC")
    List<Expense> findApprovedExpensesByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get total approved amount by currency
     * 
     * @return list of currency totals
     */
    @Query("SELECT e.currency, SUM(e.amount), SUM(e.amountInr), COUNT(e) " +
           "FROM Expense e " +
           "WHERE e.status = 'APPROVED' " +
           "AND e.isDeleted = false " +
           "GROUP BY e.currency " +
           "ORDER BY e.currency")
    List<Object[]> getTotalApprovedAmountByCurrency();

    /**
     * Get total approved amount by employee
     * 
     * @param employeeIds list of employee IDs
     * @param startDate   start date
     * @param endDate     end date
     * @return list of employee totals
     */
    @Query("SELECT e.employee.employeeId, SUM(e.amountInr), COUNT(e) " +
           "FROM Expense e " +
           "WHERE e.employee.employeeId IN :employeeIds " +
           "AND e.status = 'APPROVED' " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "AND e.isDeleted = false " +
           "GROUP BY e.employee.employeeId " +
           "ORDER BY SUM(e.amountInr) DESC")
    List<Object[]> getTotalApprovedAmountByEmployee(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get currency-wise totals by employee
     * 
     * @param employeeId employee ID
     * @param startDate  start date
     * @param endDate    end date
     * @return list of currency totals
     */
    @Query("SELECT e.currency, SUM(e.amount), SUM(e.amountInr), COUNT(e) " +
           "FROM Expense e " +
           "WHERE e.employee.employeeId = :employeeId " +
           "AND e.status = 'APPROVED' " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "AND e.isDeleted = false " +
           "GROUP BY e.currency " +
           "ORDER BY e.currency")
    List<Object[]> getCurrencyTotalsByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get total approved amount in INR
     * 
     * @return total amount in INR
     */
    @Query("SELECT COALESCE(SUM(e.amountInr), 0) " +
           "FROM Expense e " +
           "WHERE e.status = 'APPROVED' " +
           "AND e.isDeleted = false")
    BigDecimal getTotalApprovedAmountInr();
}