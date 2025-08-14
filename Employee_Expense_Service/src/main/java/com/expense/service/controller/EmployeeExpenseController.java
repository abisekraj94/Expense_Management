package com.expense.service.controller;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ApiResponseDto;
import com.expense.service.dto.ExpenseRequestDto;
import com.expense.service.dto.ExpenseResponseDto;
import com.expense.service.service.ExpenseService;
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
     */
    @PostMapping("/create-expense")
    public ResponseEntity<ApiResponseDto<ExpenseResponseDto>> createExpense(
            @Valid @RequestBody ExpenseRequestDto expenseRequestDto) {
        
        log.info("Creating expense for employee: {}", expenseRequestDto.getEmployeeId());
        
        ExpenseResponseDto response = expenseService.createExpense(expenseRequestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(constants.EXPENSE_CREATED_SUCCESS, response));
    }

    /**
     * Updates an existing expense submission by employee
     * Only allows updates for expenses in 'Requested' status by the expense owner
     * 
     * @param expenseId ID of the expense to update
     * @param expenseRequestDto Updated expense details
     * @param employeeId ID of the employee making the update
     * @return ResponseEntity with updated expense details
     */
    @PutMapping("/update-expense/{expenseId}")
    public ResponseEntity<ApiResponseDto<ExpenseResponseDto>> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @RequestParam Long employeeId) {
        
        log.info("Updating expense {} for employee: {}", expenseId, employeeId);
        
        ExpenseResponseDto response = expenseService.updateExpense(expenseId, expenseRequestDto, employeeId);
        
        return ResponseEntity.ok(ApiResponseDto.success(constants.EXPENSE_UPDATED_SUCCESS, response));
    }

    /**
     * Deletes an expense submission by employee
     * Only allows deletion of own expenses in 'Requested' status
     * 
     * @param expenseId ID of the expense to delete
     * @param employeeId ID of the employee requesting deletion
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/delete-expense/{expenseId}")
    public ResponseEntity<ApiResponseDto<String>> deleteExpense(
            @PathVariable Long expenseId,
            @RequestParam Long employeeId) {
        
        log.info("Deleting expense {} for employee: {}", expenseId, employeeId);
        
        expenseService.deleteExpense(expenseId, employeeId);
        
        return ResponseEntity.ok(ApiResponseDto.success(constants.EXPENSE_DELETED_SUCCESS, null));
    }
}