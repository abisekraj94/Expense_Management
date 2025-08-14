package com.finance.admin.service;

import com.finance.admin.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for expense management operations
 * Defines business logic for expense approval workflow
 * 
 * @author Finance Team
 * @version 1.0.0
 */
public interface ExpenseService {

    /**
     * Get all pending expenses with pagination
     * 
     * @param pageable pagination information
     * @return page of pending expenses
     */
    Page<ExpenseDto> getPendingExpenses(Pageable pageable);

    /**
     * Approve an expense
     * 
     * @param approvalRequest approval request details
     * @return updated expense
     */
    ExpenseDto approveExpense(ApprovalRequestDto approvalRequest);

    /**
     * Reject an expense with reason
     * 
     * @param rejectionRequest rejection request details
     * @return updated expense
     */
    ExpenseDto rejectExpense(RejectionRequestDto rejectionRequest);

    /**
     * Get expense by ID
     * 
     * @param expenseId expense identifier
     * @return expense details
     */
    ExpenseDto getExpenseById(Long expenseId);

    /**
     * Get total approved amount by currency
     * 
     * @return list of currency totals
     */
    List<ExpenseReportDto.CurrencyTotalDto> getTotalApprovedAmountByCurrency();

    /**
     * Get total approved amount in INR
     * 
     * @return total amount in INR
     */
    BigDecimal getTotalApprovedAmountInr();

    /**
     * Generate expense report by employee
     * 
     * @param employeeIds list of employee IDs (max 5)
     * @param startDate   start date for filtering
     * @param endDate     end date for filtering
     * @param pageable    pagination information
     * @return list of expense reports
     */
    Page<ExpenseReportDto> generateExpenseReport(
            List<Long> employeeIds, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable);

    /**
     * Sync expense data from Employee Service
     * 
     * @param expenseId expense ID to sync
     * @return synced expense
     */
    ExpenseDto syncExpenseFromEmployeeService(Long expenseId);
}