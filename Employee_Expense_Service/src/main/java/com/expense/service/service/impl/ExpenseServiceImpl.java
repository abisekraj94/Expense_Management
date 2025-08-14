package com.expense.service.service.impl;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ExpenseRequestDto;
import com.expense.service.dto.ExpenseResponseDto;
import com.expense.service.dto.ExpenseUpdateDto;
import com.expense.service.entity.EmployeeExpense;
import com.expense.service.entity.EmployeeExpenseDoc;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseNotFoundException;
import com.expense.service.exception.GlobalExceptionHandler.UnauthorizedAccessException;
import com.expense.service.repository.EmployeeExpenseDocRepository;
import com.expense.service.repository.EmployeeExpenseRepository;
import com.expense.service.repository.ExpenseCategoryRepository;
import com.expense.service.service.CurrencyService;
import com.expense.service.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService
 * Handles all expense-related business logic with proper exception handling
 */
@Service
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final EmployeeExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final EmployeeExpenseDocRepository docRepository;
    private final CurrencyService currencyService;
    private final ModelMapper modelMapper;
    private final ApplicationConstants constants;

    /**
     * Constructor for ExpenseServiceImpl
     * Initializes all required dependencies for expense management operations
     * 
     * @param expenseRepository Repository for expense data operations
     * @param categoryRepository Repository for expense category operations
     * @param docRepository Repository for expense document operations
     * @param currencyService Service for currency conversion operations
     * @param modelMapper Mapper for DTO-Entity conversions
     * @param constants Application constants for configuration values
     */
    public ExpenseServiceImpl(EmployeeExpenseRepository expenseRepository,
                             ExpenseCategoryRepository categoryRepository,
                             EmployeeExpenseDocRepository docRepository,
                             CurrencyService currencyService,
                             ModelMapper modelMapper,
                             ApplicationConstants constants) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.docRepository = docRepository;
        this.currencyService = currencyService;
        this.modelMapper = modelMapper;
        this.constants = constants;
    }

    /**
     * Creates a new expense submission for an employee
     * Validates expense category, converts currency to INR, and saves expense with documents
     * 
     * @param expenseRequestDto Request DTO containing expense details
     * @return ExpenseResponseDto with created expense information
     * @throws ExpenseNotFoundException if expense category is not found
     */
    @Override
    public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
        log.info("Creating expense for employee: {}", expenseRequestDto.getEmployeeId());

        ExpenseCategory category = categoryRepository.findByIdAndIsActiveTrue(expenseRequestDto.getExpenseCategoryId())
                .orElseThrow(() -> new ExpenseNotFoundException(constants.CATEGORY_NOT_FOUND));

        // Convert amount to INR
        BigDecimal amountInr = currencyService.convertCurrency(
                expenseRequestDto.getAmount(),
                expenseRequestDto.getCurrency(),
                constants.BASE_CURRENCY
        );

        EmployeeExpense expense = modelMapper.map(expenseRequestDto, EmployeeExpense.class);
        expense.setExpenseCategory(category);
        expense.setAmountInr(amountInr);
        expense.setStatus(constants.STATUS_REQUESTED);

        EmployeeExpense savedExpense = expenseRepository.save(expense);

        // Save documents if provided
        if (expenseRequestDto.getDocuments() != null && !expenseRequestDto.getDocuments().isEmpty()) {
            saveExpenseDocuments(savedExpense, expenseRequestDto.getDocuments());
        }

        log.info("Expense created successfully with ID: {}", savedExpense.getId());
        return mapToResponseDto(savedExpense);
    }

    /**
     * Updates an existing expense submission by employee
     * Only allows updates for expenses in 'Requested' status by the expense owner
     * 
     * @param expenseId ID of the expense to update
     * @param expenseRequestDto Updated expense details
     * @param employeeId ID of the employee making the update
     * @return ExpenseResponseDto with updated expense information
     * @throws ExpenseNotFoundException if expense or category is not found
     * @throws UnauthorizedAccessException if employee is not authorized or expense status is not 'Requested'
     */
    @Override
    public ExpenseResponseDto updateExpense(Long expenseId, ExpenseRequestDto expenseRequestDto, Long employeeId) {
        log.info("Updating expense {} for employee: {}", expenseId, employeeId);

        EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

        // Check authorization and status
        if (!expense.getEmployeeId().equals(employeeId)) {
            throw new UnauthorizedAccessException(constants.UNAUTHORIZED_ACCESS);
        }

        if (!constants.STATUS_REQUESTED.equals(expense.getStatus())) {
            throw new UnauthorizedAccessException("Can only update expenses in Requested status");
        }

        ExpenseCategory category = categoryRepository.findByIdAndIsActiveTrue(expenseRequestDto.getExpenseCategoryId())
                .orElseThrow(() -> new ExpenseNotFoundException(constants.CATEGORY_NOT_FOUND));

        // Convert amount to INR
        BigDecimal amountInr = currencyService.convertCurrency(
                expenseRequestDto.getAmount(),
                expenseRequestDto.getCurrency(),
                constants.BASE_CURRENCY
        );

        modelMapper.map(expenseRequestDto, expense);
        expense.setExpenseCategory(category);
        expense.setAmountInr(amountInr);

        EmployeeExpense updatedExpense = expenseRepository.save(expense);

        log.info("Expense updated successfully: {}", expenseId);
        return mapToResponseDto(updatedExpense);
    }

    /**
     * Updates expense status and reviewer information (Admin operation)
     * Used by administrators to approve, reject, or change expense status
     * 
     * @param expenseId ID of the expense to update
     * @param expenseUpdateDto DTO containing status and reviewer information
     * @return ExpenseResponseDto with updated expense information
     * @throws ExpenseNotFoundException if expense is not found
     */
    @Override
    public ExpenseResponseDto updateExpenseStatus(Long expenseId, ExpenseUpdateDto expenseUpdateDto) {
        log.info("Updating expense status for expense: {}", expenseId);

        EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

        modelMapper.map(expenseUpdateDto, expense);

        EmployeeExpense updatedExpense = expenseRepository.save(expense);

        log.info("Expense status updated successfully: {}", expenseId);
        return mapToResponseDto(updatedExpense);
    }

    /**
     * Soft deletes an expense by employee (Employee operation)
     * Only allows deletion of own expenses in 'Requested' status
     * 
     * @param expenseId ID of the expense to delete
     * @param employeeId ID of the employee requesting deletion
     * @throws UnauthorizedAccessException if employee cannot delete the expense
     * @throws ExpenseNotFoundException if expense is not found
     */
    @Override
    public void deleteExpense(Long expenseId, Long employeeId) {
        log.info("Deleting expense {} for employee: {}", expenseId, employeeId);

        if (!expenseRepository.existsByIdAndEmployeeIdAndStatusRequested(expenseId, employeeId)) {
            throw new UnauthorizedAccessException("Can only delete own expenses in Requested status");
        }

        EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

        expense.setIsActive(false);
        expenseRepository.save(expense);

        log.info("Expense deleted successfully: {}", expenseId);
    }

    /**
     * Soft deletes an expense by admin (Admin operation)
     * Administrators can delete any expense regardless of status
     * 
     * @param expenseId ID of the expense to delete
     * @throws ExpenseNotFoundException if expense is not found
     */
    @Override
    public void deleteExpenseByAdmin(Long expenseId) {
        log.info("Admin deleting expense: {}", expenseId);

        EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

        expense.setIsActive(false);
        expenseRepository.save(expense);

        log.info("Expense deleted by admin: {}", expenseId);
    }

    /**
     * Retrieves all active expenses for a specific employee
     * Returns expenses ordered by creation date (newest first)
     * 
     * @param employeeId ID of the employee
     * @return List of ExpenseResponseDto containing employee's expenses
     */
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponseDto> getExpensesByEmployeeId(Long employeeId) {
        log.info("Fetching expenses for employee: {}", employeeId);

        List<EmployeeExpense> expenses = expenseRepository.findByEmployeeIdAndIsActiveTrue(employeeId);
        return expenses.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific expense by its ID
     * 
     * @param expenseId ID of the expense to retrieve
     * @return ExpenseResponseDto containing expense details
     * @throws ExpenseNotFoundException if expense is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponseDto getExpenseById(Long expenseId) {
        log.info("Fetching expense by ID: {}", expenseId);

        EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

        return mapToResponseDto(expense);
    }

    /**
     * Saves expense documents associated with an expense
     * Creates EmployeeExpenseDoc entities for each document string
     * 
     * @param expense The expense entity to associate documents with
     * @param documents List of document strings to save
     */
    private void saveExpenseDocuments(EmployeeExpense expense, List<String> documents) {
        List<EmployeeExpenseDoc> docEntities = documents.stream()
                .map(doc -> {
                    EmployeeExpenseDoc docEntity = new EmployeeExpenseDoc();
                    docEntity.setEmployeeExpense(expense);
                    docEntity.setDocuments(doc);
                    return docEntity;
                })
                .collect(Collectors.toList());

        docRepository.saveAll(docEntities);
    }

    /**
     * Maps EmployeeExpense entity to ExpenseResponseDto
     * Includes category name and active documents in the response
     * 
     * @param expense The expense entity to map
     * @return ExpenseResponseDto with complete expense information
     */
    private ExpenseResponseDto mapToResponseDto(EmployeeExpense expense) {
        ExpenseResponseDto dto = modelMapper.map(expense, ExpenseResponseDto.class);
        dto.setCategoryName(expense.getExpenseCategory().getCategory());
        
        // Map documents
        if (expense.getDocuments() != null) {
            List<String> documents = expense.getDocuments().stream()
                    .filter(doc -> doc.getIsActive())
                    .map(EmployeeExpenseDoc::getDocuments)
                    .collect(Collectors.toList());
            dto.setDocuments(documents);
        }
        
        return dto;
    }
}