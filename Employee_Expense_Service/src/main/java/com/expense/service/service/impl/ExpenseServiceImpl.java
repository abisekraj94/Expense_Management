package com.expense.service.service.impl;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ExpenseRequest;
import com.expense.service.dto.ExpenseResponse;
import com.expense.service.dto.ExpenseUpdate;
import com.expense.service.dto.ExpenseUpdateRequest;
import com.expense.service.entity.EmployeeExpense;
import com.expense.service.entity.EmployeeExpenseDoc;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseNotFoundException;
import com.expense.service.exception.GlobalExceptionHandler.UnauthorizedAccessException;
import com.expense.service.exception.GlobalExceptionHandler.CurrencyConversionException;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseCategoryNotFoundException;
import com.expense.service.exception.GlobalExceptionHandler.InvalidExpenseStatusException;
import com.expense.service.exception.GlobalExceptionHandler.BusinessRuleViolationException;
import com.expense.service.exception.GlobalExceptionHandler.DatabaseOperationException;
import com.expense.service.repository.EmployeeExpenseDocRepository;
import com.expense.service.repository.EmployeeExpenseRepository;
import com.expense.service.repository.ExpenseCategoryRepository;
import com.expense.service.service.CurrencyService;
import com.expense.service.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
     * @throws ExpenseCategoryNotFoundException if expense category is not found
     * @throws CurrencyConversionException if currency conversion fails
     * @throws BusinessRuleViolationException if business rules are violated
     */
    @Override
    public ExpenseResponse createExpense(ExpenseRequest expenseRequestDto) throws ExpenseCategoryNotFoundException, CurrencyConversionException, BusinessRuleViolationException, DatabaseOperationException {
        log.info("Creating expense for employee: {}", expenseRequestDto.getEmployeeId());

        try {
            // Validate input data
            validateExpenseRequest(expenseRequestDto);

            ExpenseCategory category = categoryRepository.findByIdAndIsActiveTrue(expenseRequestDto.getExpenseCategoryId())
                    .orElseThrow(() -> new ExpenseCategoryNotFoundException(constants.CATEGORY_NOT_FOUND));

            // Convert amount to INR
            BigDecimal amountInr = currencyService.convertCurrency(
                    expenseRequestDto.getAmount(),
                    expenseRequestDto.getCurrency(),
                    constants.BASE_CURRENCY
            );

            // Check spending limits
            validateSpendingLimit(category, amountInr, expenseRequestDto.getEmployeeId());

            EmployeeExpense expense = new EmployeeExpense();
            expense.setEmployeeId(expenseRequestDto.getEmployeeId());
            expense.setExpenseCategory(category);
            expense.setCurrency(expenseRequestDto.getCurrency());
            expense.setAmount(expenseRequestDto.getAmount());
            expense.setAmountInr(amountInr);
            expense.setDescription(expenseRequestDto.getDescription());
            expense.setDateOfExpense(expenseRequestDto.getDateOfExpense());
            expense.setStatus(constants.STATUS_REQUESTED);
            expense.setIsActive(true);

            EmployeeExpense savedExpense = expenseRepository.save(expense);

            // Save documents if provided
            if (expenseRequestDto.getDocuments() != null && !expenseRequestDto.getDocuments().isEmpty()) {
                saveExpenseDocuments(savedExpense, expenseRequestDto.getDocuments());
            }

            log.info("Expense created successfully with ID: {}", savedExpense.getId());
            return mapToResponseDto(savedExpense);

        } catch (ExpenseCategoryNotFoundException | CurrencyConversionException | BusinessRuleViolationException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating expense: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to save expense due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating expense: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to create expense", e);
        }
    }

    /**
     * Updates an existing expense submission by employee
     * Only allows updates for expenses in 'Requested' status by the expense owner
     *
     * @param expenseId ID of the expense to update
     //* @param expenseRequestDto Updated expense details
     * @param employeeId ID of the employee making the update
     * @return ExpenseResponseDto with updated expense information
     * @throws ExpenseNotFoundException if expense is not found
     * @throws UnauthorizedAccessException if employee is not authorized
     * @throws ExpenseCategoryNotFoundException if expense category is not found
     * @throws CurrencyConversionException if currency conversion fails
     * @throws InvalidExpenseStatusException if expense status is invalid for update
     */
    @Override
    public ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest expenseUpdateDto, Long employeeId)
            throws ExpenseNotFoundException, UnauthorizedAccessException, ExpenseCategoryNotFoundException,
                   CurrencyConversionException, InvalidExpenseStatusException {
        log.info("Updating expense {} for employee: {}", expenseId, employeeId);

        EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

        // Check authorization and status
        if (!expense.getEmployeeId().equals(employeeId)) {
            throw new UnauthorizedAccessException(constants.UNAUTHORIZED_ACCESS);
        }

        if (!constants.STATUS_REQUESTED.equals(expense.getStatus())) {
            throw new InvalidExpenseStatusException(constants.CAN_ONLY_UPDATE_REQUESTED);
        }

        // Update only provided fields
        if (expenseUpdateDto.getExpenseCategoryId() != null) {
            ExpenseCategory category = categoryRepository.findByIdAndIsActiveTrue(expenseUpdateDto.getExpenseCategoryId())
                    .orElseThrow(() -> new ExpenseCategoryNotFoundException(constants.CATEGORY_NOT_FOUND));
            expense.setExpenseCategory(category);
        }

        if (expenseUpdateDto.getCurrency() != null) {
            expense.setCurrency(expenseUpdateDto.getCurrency());
        }

        if (expenseUpdateDto.getAmount() != null) {
            expense.setAmount(expenseUpdateDto.getAmount());
            // Recalculate INR amount
            BigDecimal amountInr = currencyService.convertCurrency(
                    expenseUpdateDto.getAmount(),
                    expense.getCurrency(),
                    constants.BASE_CURRENCY
            );
            expense.setAmountInr(amountInr);
        }

        if (expenseUpdateDto.getDescription() != null) {
            expense.setDescription(expenseUpdateDto.getDescription());
        }

        if (expenseUpdateDto.getDateOfExpense() != null) {
            expense.setDateOfExpense(expenseUpdateDto.getDateOfExpense());
        }

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
    public ExpenseResponse updateExpenseStatus(Long expenseId, ExpenseUpdate expenseUpdateDto) throws ExpenseNotFoundException {
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
     * @throws ExpenseNotFoundException if expense is not found
     * @throws UnauthorizedAccessException if employee is not authorized
     */
    @Override
    public void deleteExpense(Long expenseId, Long employeeId) throws ExpenseNotFoundException, UnauthorizedAccessException {
        log.info("Deleting expense {} for employee: {}", expenseId, employeeId);

        if (!expenseRepository.existsByIdAndEmployeeIdAndStatus(expenseId, employeeId, constants.STATUS_REQUESTED)) {
            throw new UnauthorizedAccessException(constants.CAN_ONLY_DELETE_REQUESTED);
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
    public void deleteExpenseByAdmin(Long expenseId) throws ExpenseNotFoundException {
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
    public List<ExpenseResponse> getExpensesByEmployeeId(Long employeeId) throws DatabaseOperationException {
        log.info("Fetching expenses for employee: {}", employeeId);

        try {
            List<EmployeeExpense> expenses = expenseRepository.findByEmployeeIdAndIsActiveTrue(employeeId);
            return expenses.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Database error while fetching expenses for employee {}: {}", employeeId, e.getMessage());
            throw new DatabaseOperationException("Failed to fetch expenses due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching expenses for employee {}: {}", employeeId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to fetch expenses", e);
        }
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
    public ExpenseResponse getExpenseById(Long expenseId) throws ExpenseNotFoundException, DatabaseOperationException {
        log.info("Fetching expense by ID: {}", expenseId);

        if (expenseId == null || expenseId <= 0) {
            throw new IllegalArgumentException(constants.INVALID_EMPLOYEE_ID);
        }

        try {
            EmployeeExpense expense = expenseRepository.findByIdAndIsActiveTrue(expenseId)
                    .orElseThrow(() -> new ExpenseNotFoundException(constants.EXPENSE_NOT_FOUND));

            return mapToResponseDto(expense);
        } catch (ExpenseNotFoundException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while fetching expense {}: {}", expenseId, e.getMessage());
            throw new DatabaseOperationException("Failed to fetch expense due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching expense {}: {}", expenseId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to fetch expense", e);
        }
    }

    /**
     * Validates expense request data
     */
    private void validateExpenseRequest(ExpenseRequest request) throws BusinessRuleViolationException {
        if (request.getEmployeeId() == null || request.getEmployeeId() <= 0) {
            throw new BusinessRuleViolationException("Invalid employee ID");
        }
        
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Amount must be greater than zero");
        }
        
        if (!StringUtils.hasText(request.getCurrency())) {
            throw new BusinessRuleViolationException("Currency is required");
        }
        
        if (!StringUtils.hasText(request.getDescription())) {
            throw new BusinessRuleViolationException("Description is required");
        }
        
        if (request.getDateOfExpense() == null) {
            throw new BusinessRuleViolationException("Date of expense is required");
        }
    }

    /**
     * Validates spending limits against category limits
     */
    private void validateSpendingLimit(ExpenseCategory category, BigDecimal amountInr, Long employeeId) throws BusinessRuleViolationException {
        if (category.getMaxLimit() != null && amountInr.compareTo(category.getMaxLimit()) > 0) {
            throw new BusinessRuleViolationException(
                String.format("Amount %.2f exceeds category spending limit of %.2f", 
                    amountInr, category.getMaxLimit()));
        }
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
    private ExpenseResponse mapToResponseDto(EmployeeExpense expense) {
        ExpenseResponse dto = new ExpenseResponse();
        dto.setId(expense.getId());
        dto.setEmployeeId(expense.getEmployeeId());
        dto.setCategoryName(expense.getExpenseCategory().getCategory());
        dto.setCurrency(expense.getCurrency());
        dto.setAmount(expense.getAmount());
        dto.setAmountInr(expense.getAmountInr());
        dto.setDescription(expense.getDescription());
        dto.setDateOfExpense(expense.getDateOfExpense());
        dto.setStatus(expense.getStatus());
        dto.setReviewedBy(expense.getReviewedBy());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUpdatedAt(expense.getUpdatedAt());
        
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