package com.expense.management.repository;

import com.expense.management.entity.EmployeeExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmployeeExpense entity
 * Provides database operations for employee expense management
 * 
 * @author System
 * @version 1.0.0
 */
@Repository
public interface EmployeeExpenseRepository extends JpaRepository<EmployeeExpense, Integer> {

    /**
     * Finds expenses by employee ID with pagination
     * 
     * @param empId employee ID
     * @param pageable pagination information
     * @return Page of employee expenses
     */
    Page<EmployeeExpense> findByEmployeeIdAndIsDeletedFalse(Long empId, Pageable pageable);

    /**
     * Finds expenses by status with pagination
     * 
     * @param status expense status
     * @param pageable pagination information
     * @return Page of employee expenses
     */
    Page<EmployeeExpense> findByStatusAndIsDeletedFalse(String status, Pageable pageable);

    /**
     * Finds approved expenses by employee ID within date range
     * 
     * @param empId employee ID
     * @param startDate start date
     * @param endDate end date
     * @return List of approved expenses
     */
    @Query("SELECT e FROM EmployeeExpense e WHERE e.employee.id = :empId " +
           "AND e.status = 'APPROVED' AND e.isDeleted = false " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate")
    List<EmployeeExpense> findApprovedExpensesByEmployeeAndDateRange(
            @Param("empId") Long empId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    /**
     * Gets total approved amount by currency for all employees
     * 
     * @return List of currency totals
     */
    @Query("SELECT e.currency, SUM(e.amount), SUM(e.amountInr), COUNT(e) " +
           "FROM EmployeeExpense e WHERE e.status = 'APPROVED' AND e.isDeleted = false " +
           "GROUP BY e.currency")
    List<Object[]> getTotalApprovedAmountByCurrency();

    /**
     * Gets total approved amount in INR for an employee
     * 
     * @param empId employee ID
     * @return total approved amount in INR
     */
    @Query("SELECT COALESCE(SUM(e.amountInr), 0) FROM EmployeeExpense e " +
           "WHERE e.employee.id = :empId AND e.status = 'APPROVED' AND e.isDeleted = false")
    BigDecimal getTotalApprovedAmountInrForEmployee(@Param("empId") Long empId);

    /**
     * Finds expenses within date range with pagination
     * 
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination information
     * @return Page of expenses
     */
    @Query("SELECT e FROM EmployeeExpense e WHERE e.isDeleted = false " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate")
    Page<EmployeeExpense> findByExpenseDateBetweenAndIsDeletedFalse(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    Optional<EmployeeExpense> findByIdAndIsDeletedFalse(Integer expenseId);
}