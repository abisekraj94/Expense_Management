package com.expense.service.service;

import com.expense.service.dto.ExpenseRequestDto;
import com.expense.service.dto.ExpenseResponseDto;
import com.expense.service.dto.ExpenseUpdateDto;

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
     */
    ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto);

    /**
     * Update an existing expense (employee perspective)
     * @param expenseId Expense ID to update
     * @param expenseRequestDto Updated expense data
     * @param employeeId Employee ID for authorization
     * @return Updated expense details
     */
    ExpenseResponseDto updateExpense(Long expenseId, ExpenseRequestDto expenseRequestDto, Long employeeId);

    /**
     * Update expense status (admin perspective)
     * @param expenseId Expense ID to update
     * @param expenseUpdateDto Status update data
     * @return Updated expense details
     */
    ExpenseResponseDto updateExpenseStatus(Long expenseId, ExpenseUpdateDto expenseUpdateDto);

    /**
     * Delete an expense (employee perspective)
     * @param expenseId Expense ID to delete
     * @param employeeId Employee ID for authorization
     */
    void deleteExpense(Long expenseId, Long employeeId);

    /**
     * Delete an expense (admin perspective)
     * @param expenseId Expense ID to delete
     */
    void deleteExpenseByAdmin(Long expenseId);

    /**
     * Get expenses by employee ID
     * @param employeeId Employee ID
     * @return List of employee expenses
     */
    List<ExpenseResponseDto> getExpensesByEmployeeId(Long employeeId);

    /**
     * Get expense by ID
     * @param expenseId Expense ID
     * @return Expense details
     */
    ExpenseResponseDto getExpenseById(Long expenseId);
}