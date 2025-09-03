package com.expense.management.service;

import com.expense.management.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for expense management operations
 * Defines contract for expense-related business operations
 * 
 * @author System
 * @version 1.0.0
 */
public interface ExpenseService {

    /**
     * Submits a new expense
     * 
     * @param expenseDto expense submission data
     * @return created expense response
     */
    ExpenseResponse submitExpense(ExpenseSubmission expenseDto);

    /**
     * Gets expenses by employee ID with pagination
     * 
     * @param empId employee ID
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByEmployee(Long empId, Pageable pageable);

    /**
     * Gets expenses by status with pagination
     * 
     * @param status expense status
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByStatus(String status, Pageable pageable);

    /**
     * Gets expense by ID
     * 
     * @param expenseId expense ID
     * @return expense response
     */
    ExpenseResponse getExpenseById(Integer expenseId);

    /**
     * Approves or rejects an expense
     * 
     * @param expenseId expense ID
     * @param approvalDto approval data
     * @return updated expense response
     */
    ExpenseResponse approveOrRejectExpense(Integer expenseId, ExpenseApproval approvalDto);

    /**
     * Gets expenses within date range with pagination
     * 
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Gets expenses within date range with pagination and sorting
     * 
     * @param startDate start date
     * @param endDate end date
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String sortDir);

    /**
     * Gets total approved expenses by currency
     * 
     * @return list of currency totals
     */
    List<ExpenseReport.CurrencyTotalDto> getTotalApprovedExpensesByCurrency();
}