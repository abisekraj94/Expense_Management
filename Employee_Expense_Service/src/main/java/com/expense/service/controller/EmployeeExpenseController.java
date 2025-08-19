package com.expense.service.controller;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ApiResponse;
import com.expense.service.dto.ExpenseRequest;
import com.expense.service.dto.ExpenseUpdateRequest;
import com.expense.service.dto.ExpenseResponse;
import com.expense.service.dto.DeleteRequest;
import com.expense.service.service.ExpenseService;
import com.expense.service.exception.GlobalExceptionHandler.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for employee expense operations
 * Handles expense creation, updates, and deletion from employee perspective
 */
@RestController
@RequestMapping("${app.api.employee}")
@Slf4j
public class EmployeeExpenseController {

    private final ExpenseService expenseService;
    private final ApplicationConstants constants;

    /**
     * Constructor for EmployeeExpenseController
     * 
     * @param expenseService Service for expense operations
     * @param constants Application constants for messages
     */
    public EmployeeExpenseController(ExpenseService expenseService, ApplicationConstants constants) {
        this.expenseService = expenseService;
        this.constants = constants;
    }

    /**
     * Creates a new expense submission for an employee
     * Validates expense data, converts currency to INR, and saves with documents
     * 
     * @param expenseRequestDto Request DTO containing expense details
     * @return ResponseEntity with created expense details and HTTP 201 status
     * @throws ExpenseCategoryNotFoundException if expense category is not found
     * @throws CurrencyConversionException if currency conversion fails
     * @throws BusinessRuleViolationException if business rules are violated
     */
    @PostMapping("/create-expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest expenseRequestDto) 
            throws ExpenseCategoryNotFoundException, CurrencyConversionException, BusinessRuleViolationException {
        
        log.info("Creating expense for employee: {}", expenseRequestDto.getEmployeeId());
        
        try {
            ExpenseResponse response = expenseService.createExpense(expenseRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(constants.EXPENSE_CREATED_SUCCESS, response));
        } catch (ExpenseCategoryNotFoundException | CurrencyConversionException | BusinessRuleViolationException e) {
            log.error("Failed to create expense: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating expense: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create expense", e);
        }
    }

    /**
     * Updates an existing expense submission by employee
     * Only allows updates for expenses in 'Requested' status by the expense owner
     * 
     * @param expenseId ID of the expense to update
     * @param expenseRequestDto Updated expense details
     * @param employeeId ID of the employee making the update
     * @return ResponseEntity with updated expense details
     * @throws ExpenseNotFoundException if expense is not found
     * @throws UnauthorizedAccessException if employee is not authorized
     * @throws ExpenseCategoryNotFoundException if expense category is not found
     * @throws CurrencyConversionException if currency conversion fails
     * @throws InvalidExpenseStatusException if expense status is invalid for update
     */
    @PutMapping("/update-expense/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseUpdateRequest expenseUpdateDto,
            @RequestParam Long employeeId) 
            throws ExpenseNotFoundException, UnauthorizedAccessException, ExpenseCategoryNotFoundException, 
                   CurrencyConversionException, InvalidExpenseStatusException {
        
        log.info("Updating expense {} for employee: {}", expenseId, employeeId);
        
        try {
            ExpenseResponse response = expenseService.updateExpense(expenseId, expenseUpdateDto, employeeId);
            return ResponseEntity.ok(ApiResponse.success(constants.EXPENSE_UPDATED_SUCCESS, response));
        } catch (ExpenseNotFoundException | UnauthorizedAccessException | ExpenseCategoryNotFoundException | 
                 CurrencyConversionException | InvalidExpenseStatusException e) {
            log.error("Failed to update expense {}: {}", expenseId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating expense {}: {}", expenseId, e.getMessage(), e);
            throw new RuntimeException("Failed to update expense", e);
        }
    }

    /**
     * Deletes an expense submission by employee
     * Only allows deletion of own expenses in 'Requested' status
     * 
     * @param expenseId ID of the expense to delete
     * @param deleteRequest Request containing employeeId
     * @return ResponseEntity with success message
     * @throws ExpenseNotFoundException if expense is not found
     * @throws UnauthorizedAccessException if employee is not authorized
     */
    @DeleteMapping("/delete-expense/{expenseId}")
    public ResponseEntity<ApiResponse<String>> deleteExpense(
            @PathVariable Long expenseId,
            @RequestBody DeleteRequest deleteRequest) 
            throws ExpenseNotFoundException, UnauthorizedAccessException {
        
        log.info("Deleting expense {} for employee: {}", expenseId, deleteRequest.getEmployeeId());
        
        try {
            expenseService.deleteExpense(expenseId, deleteRequest.getEmployeeId());
            return ResponseEntity.ok(ApiResponse.success(constants.EXPENSE_DELETED_SUCCESS, null));
        } catch (ExpenseNotFoundException | UnauthorizedAccessException e) {
            log.error("Failed to delete expense {}: {}", expenseId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting expense {}: {}", expenseId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete expense", e);
        }
    }
}