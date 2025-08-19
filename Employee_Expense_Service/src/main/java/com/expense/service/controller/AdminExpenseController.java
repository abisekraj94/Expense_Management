package com.expense.service.controller;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ApiResponse;
import com.expense.service.dto.CategoryRequest;
import com.expense.service.dto.ExpenseResponse;
import com.expense.service.dto.ExpenseUpdate;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.service.CategoryService;
import com.expense.service.service.ExpenseService;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for admin expense operations
 * Handles expense management from admin perspective
 */
@RestController
@RequestMapping("${app.api.admin}")
@Slf4j
public class AdminExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final ApplicationConstants constants;

    /**
     * Constructor for AdminExpenseController
     * 
     * @param expenseService Service for expense operations
     * @param categoryService Service for category operations
     * @param constants Application constants for messages
     */
    public AdminExpenseController(ExpenseService expenseService, CategoryService categoryService, ApplicationConstants constants) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.constants = constants;
    }

    /**
     * Creates a new expense category (Admin operation)
     * 
     * @param categoryRequest DTO containing category details
     * @return ResponseEntity containing created category
     */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<ExpenseCategory>> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {
        
        log.info("Admin creating expense category: {}", categoryRequest.getName());
        
        try {
            ExpenseCategory category = categoryService.createCategory(categoryRequest);
            return ResponseEntity.ok(ApiResponse.success("Category created successfully", category));
        } catch (Exception e) {
            log.error("Error creating category {}: {}", categoryRequest.getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to create category", e);
        }
    }

    /**
     * Retrieves all expenses for a specific employee (Admin operation)
     * Allows administrators to view all expenses submitted by an employee
     * 
     * @param employeeId ID of the employee whose expenses to retrieve
     * @return ResponseEntity containing list of employee expenses
     */
    @GetMapping("/get/{employeeId}")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByEmployeeId(
            @PathVariable Long employeeId) {
        
        log.info("Admin fetching expenses for employee: {}", employeeId);
        
        try {
            if (employeeId == null || employeeId <= 0) {
                throw new IllegalArgumentException("Invalid employee ID");
            }
            
            List<ExpenseResponse> expenses = expenseService.getExpensesByEmployeeId(employeeId);
            return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", expenses));
        } catch (IllegalArgumentException e) {
            log.error("Invalid employee ID {}: {}", employeeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching expenses for employee {}: {}", employeeId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch expenses", e);
        }
    }

    /**
     * Updates expense status and reviewer information (Admin operation)
     * Allows administrators to approve, reject, or change expense status
     * 
     * @param expenseId ID of the expense to update
     * @param expenseUpdateDto DTO containing new status and reviewer information
     * @return ResponseEntity containing updated expense details
     * @throws ExpenseNotFoundException if expense is not found
     */
    @PutMapping("/update-expense/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpenseStatus(
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseUpdate expenseUpdateDto) throws ExpenseNotFoundException {
        
        log.info("Admin updating expense status for expense: {}", expenseId);
        
        try {
            ExpenseResponse response = expenseService.updateExpenseStatus(expenseId, expenseUpdateDto);
            return ResponseEntity.ok(ApiResponse.success(constants.EXPENSE_UPDATED_SUCCESS, response));
        } catch (ExpenseNotFoundException e) {
            log.error("Failed to update expense status for {}: {}", expenseId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating expense status for {}: {}", expenseId, e.getMessage(), e);
            throw new RuntimeException("Failed to update expense status", e);
        }
    }

    /**
     * Deletes an expense (Admin operation)
     * Administrators can delete any expense regardless of status or owner
     * 
     * @param expenseId ID of the expense to delete
     * @return ResponseEntity with success message
     * @throws ExpenseNotFoundException if expense is not found
     */
    @DeleteMapping("/delete-expense/{expenseId}")
    public ResponseEntity<ApiResponse<String>> deleteExpense(@PathVariable Long expenseId) throws ExpenseNotFoundException {
        
        log.info("Admin deleting expense: {}", expenseId);
        
        try {
            expenseService.deleteExpenseByAdmin(expenseId);
            return ResponseEntity.ok(ApiResponse.success(constants.EXPENSE_DELETED_SUCCESS, null));
        } catch (ExpenseNotFoundException e) {
            log.error("Failed to delete expense {}: {}", expenseId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting expense {}: {}", expenseId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete expense", e);
        }
    }
}