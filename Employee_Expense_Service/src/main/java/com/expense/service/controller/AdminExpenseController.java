package com.expense.service.controller;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ApiResponse;
import com.expense.service.dto.CategoryRequest;
import com.expense.service.dto.ExpenseResponse;
import com.expense.service.dto.ExpenseUpdate;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.service.CategoryService;
import com.expense.service.service.ExpenseService;
import com.expense.service.exception.GlobalExceptionHandler.*;
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
            @Valid @RequestBody CategoryRequest categoryRequest) throws CategoryServiceException {
        
        log.info("Admin creating expense category: {}", categoryRequest.getName());
        
        ExpenseCategory category = categoryService.createCategory(categoryRequest);
        return ResponseEntity.ok(ApiResponse.success(constants.CATEGORY_CREATED_SUCCESS, category));
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
            @PathVariable Long employeeId) throws DatabaseOperationException {
        
        log.info("Admin fetching expenses for employee: {}", employeeId);
        
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException(constants.INVALID_EMPLOYEE_ID);
        }
        
        List<ExpenseResponse> expenses = expenseService.getExpensesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(constants.EXPENSES_RETRIEVED_SUCCESS, expenses));
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
        
        ExpenseResponse response = expenseService.updateExpenseStatus(expenseId, expenseUpdateDto);
        return ResponseEntity.ok(ApiResponse.success(constants.EXPENSE_UPDATED_SUCCESS, response));
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
        
        expenseService.deleteExpenseByAdmin(expenseId);
        return ResponseEntity.ok(ApiResponse.success(constants.EXPENSE_DELETED_SUCCESS, null));
    }
}