package com.expense.management.controller;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.dto.ApiResponse;
import com.expense.management.dto.ExpenseResponse;
import com.expense.management.dto.ExpenseSubmission;
import com.expense.management.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for expense management operations
 * Handles employee expense submission and retrieval
 * 
 * @author System
 * @version 1.0.0
 */
@RestController
@RequestMapping(ApplicationConstants.EXPENSE_API_PATH)
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Constructor for ExpenseController
     * 
     * @param expenseService expense service
     */
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Submits a new expense
     * 
     * @param expenseDto expense submission data
     * @return created expense response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> submitExpense(
            @Valid @RequestBody ExpenseSubmission expenseDto) {
        log.info("Received expense submission request for employee ID: {}", expenseDto.getEmpId());
        
        ExpenseResponse response = expenseService.submitExpense(expenseDto);
        ApiResponse<ExpenseResponse> apiResponse = ApiResponse.success(
                "Expense submitted successfully", response);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Gets expenses by employee ID with pagination
     * 
     * @param empId employee ID
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy sort field (default: createdAt)
     * @param sortDir sort direction (default: desc)
     * @return page of expenses
     */
    @GetMapping("/get-employee-expenses/{empId}")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByEmployee(
            @PathVariable Long empId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting expenses for employee ID: {} with pagination", empId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, Math.min(size, ApplicationConstants.MAX_PAGE_SIZE), sort);
        
        Page<ExpenseResponse> expenses = expenseService.getExpensesByEmployee(empId, pageable);
        ApiResponse<Page<ExpenseResponse>> apiResponse = ApiResponse.success(
                "Expenses retrieved successfully", expenses);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Gets expense by ID
     * 
     * @param expenseId expense ID
     * @return expense details
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable Integer expenseId) {
        
        log.info("Getting expense by ID: {}", expenseId);
        
        ExpenseResponse expense = expenseService.getExpenseById(expenseId);
        ApiResponse<ExpenseResponse> apiResponse = ApiResponse.success(
                "Expense retrieved successfully", expense);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Gets expenses by status with pagination
     * 
     * @param status expense status
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy sort field (default: createdAt)
     * @param sortDir sort direction (default: desc)
     * @return page of expenses
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting expenses with status: {} with pagination", status);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, Math.min(size, ApplicationConstants.MAX_PAGE_SIZE), sort);
        
        Page<ExpenseResponse> expenses = expenseService.getExpensesByStatus(status, pageable);
        ApiResponse<Page<ExpenseResponse>> apiResponse = ApiResponse.success(
                "Expenses retrieved successfully", expenses);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Gets expenses within date range with pagination
     * 
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy sort field (default: expenseDate)
     * @param sortDir sort direction (default: desc)
     * @return page of expenses
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting expenses between {} and {} with pagination", startDate, endDate);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, Math.min(size, ApplicationConstants.MAX_PAGE_SIZE), sort);
        
        Page<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(startDate, endDate, pageable);
        ApiResponse<Page<ExpenseResponse>> apiResponse = ApiResponse.success(
                "Expenses retrieved successfully", expenses);
        
        return ResponseEntity.ok(apiResponse);
    }
}