package com.finance.admin.controller;

import com.finance.admin.dto.*;
import com.finance.admin.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for expense management operations
 * Handles expense approval workflow and related operations
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Get all pending expenses with pagination
     * 
     * @param pageable pagination parameters
     * @return page of pending expenses
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<Expense>> getPendingExpenses(
            @PageableDefault(size = 10) Pageable pageable) {
        
        log.info("Fetching pending expenses with pagination: {}", pageable);
        
        Page<Expense> expenses = expenseService.getPendingExpenses(pageable);
        
        log.info("Retrieved {} pending expenses", expenses.getNumberOfElements());
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get expense by ID
     * 
     * @param expenseId expense identifier
     * @return expense details
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long expenseId) {
        log.info("Fetching expense with ID: {}", expenseId);
        
        Expense expense = expenseService.getExpenseById(expenseId);
        
        return ResponseEntity.ok(expense);
    }

    /**
     * Approve an expense
     * 
     * @param approvalRequest approval request details
     * @return updated expense
     */
    @PostMapping("/approve")
    public ResponseEntity<Expense> approveExpense(
            @Valid @RequestBody ApprovalRequest approvalRequest) {
        
        log.info("Approving expense with ID: {}", approvalRequest.getExpenseId());
        
        approvalRequest.setApprovedBy("admin");
        
        Expense expense = expenseService.approveExpense(approvalRequest);
        
        log.info("Expense approved successfully: {}", expense.getExpenseId());
        return ResponseEntity.ok(expense);
    }

    /**
     * Reject an expense with reason
     * 
     * @param rejectionRequest rejection request details
     * @return updated expense
     */
    @PostMapping("/reject")
    public ResponseEntity<Expense> rejectExpense(
            @Valid @RequestBody RejectionRequest rejectionRequest) {
        
        log.info("Rejecting expense with ID: {}", rejectionRequest.getExpenseId());
        
        rejectionRequest.setRejectedBy("admin");
        
        Expense expense = expenseService.rejectExpense(rejectionRequest);
        
        log.info("Expense rejected successfully: {}", expense.getExpenseId());
        return ResponseEntity.ok(expense);
    }

    /**
     * Get total approved amount by currency
     * 
     * @return list of currency totals
     */
    @GetMapping("/totals/currency")
    public ResponseEntity<List<ExpenseReport.CurrencyTotalDto>> getTotalApprovedAmountByCurrency() {
        log.info("Fetching total approved amount by currency");
        
        List<ExpenseReport.CurrencyTotalDto> totals = expenseService.getTotalApprovedAmountByCurrency();
        
        return ResponseEntity.ok(totals);
    }

    /**
     * Get total approved amount in INR
     * 
     * @return total amount in INR
     */
    @GetMapping("/totals/inr")
    public ResponseEntity<BigDecimal> getTotalApprovedAmountInr() {
        log.info("Fetching total approved amount in INR");
        
        BigDecimal total = expenseService.getTotalApprovedAmountInr();
        
        return ResponseEntity.ok(total);
    }

    /**
     * Sync expense data from Employee Service
     * 
     * @param expenseId expense ID to sync
     * @return synced expense
     */
    @PostMapping("/{expenseId}/sync")
    public ResponseEntity<Expense> syncExpenseFromEmployeeService(@PathVariable Long expenseId) {
        log.info("Syncing expense from Employee Service: {}", expenseId);
        
        Expense expense = expenseService.syncExpenseFromEmployeeService(expenseId);
        
        return ResponseEntity.ok(expense);
    }
}