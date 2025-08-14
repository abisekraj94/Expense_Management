package com.expense.service.controller;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ApiResponseDto;
import com.expense.service.dto.ExpenseResponseDto;
import com.expense.service.dto.ExpenseUpdateDto;
import com.expense.service.service.ExpenseService;
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
    private final ApplicationConstants constants;

    /**
     * Constructor for AdminExpenseController
     * 
     * @param expenseService Service for expense operations
     * @param constants Application constants for messages
     */
    public AdminExpenseController(ExpenseService expenseService, ApplicationConstants constants) {
        this.expenseService = expenseService;
        this.constants = constants;
    }

    /**
     * Retrieves all expenses for a specific employee (Admin operation)
     * Allows administrators to view all expenses submitted by an employee
     * 
     * @param employeeId ID of the employee whose expenses to retrieve
     * @return ResponseEntity containing list of employee expenses
     */
    @GetMapping("/get/{employeeId}")
    public ResponseEntity<ApiResponseDto<List<ExpenseResponseDto>>> getExpensesByEmployeeId(
            @PathVariable Long employeeId) {
        
        log.info("Admin fetching expenses for employee: {}", employeeId);
        
        List<ExpenseResponseDto> expenses = expenseService.getExpensesByEmployeeId(employeeId);
        
        return ResponseEntity.ok(ApiResponseDto.success("Expenses retrieved successfully", expenses));
    }

    /**
     * Updates expense status and reviewer information (Admin operation)
     * Allows administrators to approve, reject, or change expense status
     * 
     * @param expenseId ID of the expense to update
     * @param expenseUpdateDto DTO containing new status and reviewer information
     * @return ResponseEntity containing updated expense details
     */
    @PutMapping("/update-expense/{expenseId}")
    public ResponseEntity<ApiResponseDto<ExpenseResponseDto>> updateExpenseStatus(
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseUpdateDto expenseUpdateDto) {
        
        log.info("Admin updating expense status for expense: {}", expenseId);
        
        ExpenseResponseDto response = expenseService.updateExpenseStatus(expenseId, expenseUpdateDto);
        
        return ResponseEntity.ok(ApiResponseDto.success(constants.EXPENSE_UPDATED_SUCCESS, response));
    }

    /**
     * Deletes an expense (Admin operation)
     * Administrators can delete any expense regardless of status or owner
     * 
     * @param expenseId ID of the expense to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/delete-expense/{expenseId}")
    public ResponseEntity<ApiResponseDto<String>> deleteExpense(@PathVariable Long expenseId) {
        
        log.info("Admin deleting expense: {}", expenseId);
        
        expenseService.deleteExpenseByAdmin(expenseId);
        
        return ResponseEntity.ok(ApiResponseDto.success(constants.EXPENSE_DELETED_SUCCESS, null));
    }
}