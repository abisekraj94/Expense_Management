package com.expense.management.controller;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.dto.ApiResponse;
import com.expense.management.dto.ExpenseApproval;
import com.expense.management.dto.ExpenseReport;
import com.expense.management.dto.ExpenseResponse;
import com.expense.management.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for finance admin operations
 * Handles expense approval/rejection and admin-specific functionalities
 * 
 * @author System
 * @version 1.0.0
 */
@RestController
@RequestMapping(ApplicationConstants.ADMIN_API_PATH)
@Slf4j
public class AdminController {

    private final ExpenseService expenseService;

    /**
     * Constructor for AdminController
     * 
     * @param expenseService expense service
     */
    public AdminController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Gets pending expenses for admin review with pagination
     * 
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy sort field (default: createdAt)
     * @param sortDir sort direction (default: asc)
     * @return page of pending expenses
     */
    @GetMapping("/expenses/{status}")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @PathVariable("status") String status) {
        
        log.info("Getting pending expenses for admin review with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ExpenseResponse> expenses = expenseService.getExpensesByStatus(status, pageable);
        ApiResponse<Page<ExpenseResponse>> apiResponse = ApiResponse.success(
                "Pending expenses retrieved successfully", expenses);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Approves or rejects an expense
     * 
     * @param expenseId expense ID
     * @param approvalDto approval/rejection data
     * @return updated expense response
     */
    @PutMapping("/expenses/{expenseId}/status-change")
    public ResponseEntity<ApiResponse<ExpenseResponse>> approveOrRejectExpense(
            @PathVariable Integer expenseId,
            @Valid @RequestBody ExpenseApproval approvalDto) {
        
        log.info("Processing approval/rejection for expense ID: {} by approver ID: {}", 
                expenseId, approvalDto);
        
        ExpenseResponse response = expenseService.approveOrRejectExpense(expenseId, approvalDto);
        ApiResponse<ExpenseResponse> apiResponse = ApiResponse.success(
                "Expense " + approvalDto.getAction().toLowerCase() + " successfully", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Gets total approved expenses by currency
     * 
     * @return list of currency totals
     */
    @GetMapping("/expenses/totals/currency")
    public ResponseEntity<ApiResponse<List<ExpenseReport.CurrencyTotalDto>>> getTotalApprovedExpensesByCurrency() {
        log.info("Getting total approved expenses by currency");
        
        List<ExpenseReport.CurrencyTotalDto> totals = expenseService.getTotalApprovedExpensesByCurrency();
        ApiResponse<List<ExpenseReport.CurrencyTotalDto>> apiResponse = ApiResponse.success(
                "Currency totals retrieved successfully", totals);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Gets all expenses with pagination (admin view)
     * 
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy sort field (default: createdAt)
     * @param sortDir sort direction (default: desc)
     * @return page of all expenses
     */
    @GetMapping("/expenses")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all expenses for admin with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, Math.min(size, ApplicationConstants.MAX_PAGE_SIZE), sort);
        
        // Get all expenses by using a broad date range
        Page<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(
                LocalDate.of(2020, 1, 1), LocalDate.now().plusDays(1), page, size, sortBy, sortDir);
        ApiResponse<Page<ExpenseResponse>> apiResponse = ApiResponse.success(
                "All expenses retrieved successfully", expenses);
        
        return ResponseEntity.ok(apiResponse);
    }
}