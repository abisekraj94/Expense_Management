package com.expense.service.service;

import com.expense.service.dto.ExpenseRequest;
import com.expense.service.dto.ExpenseUpdateRequest;
import com.expense.service.dto.ExpenseResponse;
import com.expense.service.dto.ExpenseUpdate;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseNotFoundException;
import com.expense.service.exception.GlobalExceptionHandler.UnauthorizedAccessException;
import com.expense.service.exception.GlobalExceptionHandler.CurrencyConversionException;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseCategoryNotFoundException;
import com.expense.service.exception.GlobalExceptionHandler.InvalidExpenseStatusException;
import com.expense.service.exception.GlobalExceptionHandler.BusinessRuleViolationException;

import java.util.List;

/**
 * Service interface for expense management operations
 * Defines business logic for expense CRUD operations
 */
public interface ExpenseService {

    /**
     * Create a new expense
     * @param expenseRequestDto Expense creation request
     * @return Created expense details
     * @throws ExpenseCategoryNotFoundException if expense category is not found
     * @throws CurrencyConversionException if currency conversion fails
     * @throws BusinessRuleViolationException if business rules are violated
     */
    ExpenseResponse createExpense(ExpenseRequest expenseRequestDto) throws ExpenseCategoryNotFoundException, CurrencyConversionException, BusinessRuleViolationException;

    /**
     * Update an existing expense (employee perspective)
     * @param expenseId Expense ID to update
     * @param expenseRequestDto Updated expense data
     * @param employeeId Employee ID for authorization
     * @return Updated expense details
     * @throws ExpenseNotFoundException if expense is not found
     * @throws UnauthorizedAccessException if employee is not authorized
     * @throws ExpenseCategoryNotFoundException if expense category is not found
     * @throws CurrencyConversionException if currency conversion fails
     * @throws InvalidExpenseStatusException if expense status is invalid for update
     */
    ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest expenseUpdateDto, Long employeeId) 
            throws ExpenseNotFoundException, UnauthorizedAccessException, ExpenseCategoryNotFoundException, 
                   CurrencyConversionException, InvalidExpenseStatusException;

    /**
     * Update expense status (admin perspective)
     * @param expenseId Expense ID to update
     * @param expenseUpdateDto Status update data
     * @return Updated expense details
     * @throws ExpenseNotFoundException if expense is not found
     */
    ExpenseResponse updateExpenseStatus(Long expenseId, ExpenseUpdate expenseUpdateDto) throws ExpenseNotFoundException;

    /**
     * Delete an expense (employee perspective)
     * @param expenseId Expense ID to delete
     * @param employeeId Employee ID for authorization
     * @throws ExpenseNotFoundException if expense is not found
     * @throws UnauthorizedAccessException if employee is not authorized
     */
    void deleteExpense(Long expenseId, Long employeeId) throws ExpenseNotFoundException, UnauthorizedAccessException;

    /**
     * Delete an expense (admin perspective)
     * @param expenseId Expense ID to delete
     * @throws ExpenseNotFoundException if expense is not found
     */
    void deleteExpenseByAdmin(Long expenseId) throws ExpenseNotFoundException;

    /**
     * Get expenses by employee ID
     * @param employeeId Employee ID
     * @return List of employee expenses
     */
    List<ExpenseResponse> getExpensesByEmployeeId(Long employeeId);

    /**
     * Get expense by ID
     * @param expenseId Expense ID
     * @return Expense details
     * @throws ExpenseNotFoundException if expense is not found
     */
    ExpenseResponse getExpenseById(Long expenseId) throws ExpenseNotFoundException;
}