package com.expense.management.service.impl;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.dto.ExpenseApproval;
import com.expense.management.dto.ExpenseReport;
import com.expense.management.dto.ExpenseResponse;
import com.expense.management.dto.ExpenseSubmission;
import com.expense.management.entity.Employee;
import com.expense.management.entity.EmployeeExpense;
import com.expense.management.entity.ExpenseCategory;
import com.expense.management.exception.BusinessException;
import com.expense.management.exception.ResourceNotFoundException;
import com.expense.management.repository.EmployeeExpenseRepository;
import com.expense.management.repository.EmployeeRepository;
import com.expense.management.repository.ExpenseCategoryRepository;
import com.expense.management.service.ExpenseService;
import com.expense.management.util.CurrencyExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService interface
 * Handles all expense-related business operations
 * 
 * @author System
 * @version 1.0.0
 */
@Service
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final EmployeeExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final CurrencyExchangeService currencyExchangeService;
    private final NotificationService notificationService;
    private final NotificationServiceImpl notificationServiceImpl;
    private final ModelMapper modelMapper;

    /**
     * Constructor for ExpenseServiceImpl
     */
    public ExpenseServiceImpl(EmployeeExpenseRepository expenseRepository,
                              EmployeeRepository employeeRepository,
                              ExpenseCategoryRepository categoryRepository,
                              CurrencyExchangeService currencyExchangeService,
                              NotificationService notificationService, NotificationServiceImpl notificationServiceImpl,
                              ModelMapper modelMapper) {
        this.expenseRepository = expenseRepository;
        this.employeeRepository = employeeRepository;
        this.categoryRepository = categoryRepository;
        this.currencyExchangeService = currencyExchangeService;
        this.notificationService = notificationService;
        this.notificationServiceImpl = notificationServiceImpl;
        this.modelMapper = modelMapper;
    }

    /**
     * Submits a new expense with currency conversion
     * 
     * @param expenseDto expense submission data
     * @return created expense response
     */
    @Override
    public ExpenseResponse submitExpense(ExpenseSubmission expenseDto) {
        log.info("Submitting expense for employee ID: {}", expenseDto.getEmpId());

        try {
            // Validate employee exists
            Employee employee = employeeRepository.findById(expenseDto.getEmpId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + expenseDto.getEmpId()));

            // Validate expense category exists and is active
            ExpenseCategory category = categoryRepository.findByIdAndIsActiveTrue(expenseDto.getExpenseCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Active expense category not found with ID: " + expenseDto.getExpenseCategoryId()));

            // Validate amount against category limit
            if (expenseDto.getAmount().compareTo(category.getMaxLimit()) > 0) {
                throw new BusinessException("Expense amount exceeds category limit of " + category.getMaxLimit());
            }

            // Create expense entity
            EmployeeExpense expense = new EmployeeExpense();
            expense.setEmployee(employee);
            expense.setExpenseCategory(category);
            expense.setDescription(expenseDto.getDescription());
            expense.setAmount(expenseDto.getAmount());
            expense.setCurrency(expenseDto.getCurrency());
            expense.setExpenseDate(expenseDto.getExpenseDate());

            // Convert currency to INR if not already INR
            if (!ApplicationConstants.BASE_CURRENCY.equals(expenseDto.getCurrency())) {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(expenseDto.getCurrency(), ApplicationConstants.BASE_CURRENCY);
                BigDecimal amountInr = currencyExchangeService.convertCurrency(expenseDto.getAmount(), expenseDto.getCurrency(), ApplicationConstants.BASE_CURRENCY);
                expense.setExchangeRate(exchangeRate);
                expense.setAmountInr(amountInr);
            } else {
                expense.setExchangeRate(BigDecimal.ONE);
                expense.setAmountInr(expenseDto.getAmount());
            }

            // Save expense
            EmployeeExpense savedExpense = expenseRepository.save(expense);
            log.info("Expense submitted successfully with ID: {}", savedExpense.getId());

            return mapToExpenseResponse(savedExpense);

        } catch (Exception e) {
            log.error("Error submitting expense: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets expenses by employee ID with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByEmployee(Long empId, Pageable pageable) {
        log.info("Getting expenses for employee ID: {}", empId);

        try {
            // Validate employee exists
            if (!employeeRepository.existsById(empId)) {
                throw new ResourceNotFoundException("Employee not found with ID: " + empId);
            }

            Page<EmployeeExpense> expenses = expenseRepository.findByEmployeeIdAndIsDeletedFalse(empId, pageable);
            return expenses.map(this::mapToExpenseResponse);

        } catch (Exception e) {
            log.error("Error getting expenses for employee {}: {}", empId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets expenses by status with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByStatus(String status, Pageable pageable) {
        log.info("Getting expenses with status: {}", status);

        try {
            Page<EmployeeExpense> expenses = expenseRepository.findByStatusAndIsDeletedFalse(status, pageable);
            return expenses.map(this::mapToExpenseResponse);

        } catch (Exception e) {
            log.error("Error getting expenses by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets expense by ID
     */
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Integer expenseId) {
        log.info("Getting expense by ID: {}", expenseId);

        try {
            EmployeeExpense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

            if (expense.getIsDeleted()) {
                throw new ResourceNotFoundException("Expense not found with ID: " + expenseId);
            }

            return mapToExpenseResponse(expense);

        } catch (Exception e) {
            log.error("Error getting expense by ID {}: {}", expenseId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Approves or rejects an expense
     */
    @Override
    public ExpenseResponse approveOrRejectExpense(Integer expenseId, ExpenseApproval approvalDto) {
        try {
            log.info("Processing approval for expense ID: {} by approver ID: {}", expenseId, approvalDto);
            // Validate expense exists and is pending
            EmployeeExpense expense = expenseRepository.findByIdAndIsDeletedFalse(expenseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));
            if (Objects.isNull(expense)) {
                throw new ResourceNotFoundException("Expense not found with ID: " + expenseId);
            }
            
            // Eagerly load employee data before async call
            expense.getEmployee().getEmailId();
            expense.getEmployee().getFirstName();
            expense.getEmployee().getLastName();
            
            expense.setStatus(approvalDto.getAction());
            expense.setRemarks(approvalDto.getRemarks());
            expense.setApprovalDate(LocalDate.now());
            expenseRepository.save(expense);
            
            // Send notification email
            notificationServiceImpl.sendExpenseStatusNotification(expense);
            return mapToExpenseResponse(expense);
        } catch (Exception e) {
            log.error("Error processing approval for expense {}: {}", expenseId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets expenses within date range with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("Getting expenses between {} and {}", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                throw new BusinessException("Start date cannot be after end date");
            }

            Page<EmployeeExpense> expenses = expenseRepository.findByExpenseDateBetweenAndIsDeletedFalse(startDate, endDate, pageable);
            return expenses.map(this::mapToExpenseResponse);

        } catch (Exception e) {
            log.error("Error getting expenses by date range: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets expenses within date range with pagination and sorting
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String sortDir) {
        log.info("Getting expenses between {} and {} with sorting", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                throw new BusinessException("Start date cannot be after end date");
            }

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, Math.min(size, ApplicationConstants.MAX_PAGE_SIZE), sort);

            Page<EmployeeExpense> expenses = expenseRepository.findByExpenseDateBetweenAndIsDeletedFalse(startDate, endDate, pageable);
            return expenses.map(this::mapToExpenseResponse);

        } catch (Exception e) {
            log.error("Error getting expenses by date range: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets total approved expenses by currency
     */
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseReport.CurrencyTotalDto> getTotalApprovedExpensesByCurrency() {
        log.info("Getting total approved expenses by currency");

        try {
            List<Object[]> results = expenseRepository.getTotalApprovedAmountByCurrency();
            
            return results.stream()
                    .map(result -> new ExpenseReport.CurrencyTotalDto(
                            (String) result[0],           // currency
                            (BigDecimal) result[1],       // totalAmount
                            (BigDecimal) result[2],       // totalAmountInr
                            (Long) result[3]              // expenseCount
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting total approved expenses by currency: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Safely maps EmployeeExpense to ExpenseResponse handling null approvedBy
     */
    private ExpenseResponse mapToExpenseResponse(EmployeeExpense expense) {
        try {
            return modelMapper.map(expense, ExpenseResponse.class);
        } catch (Exception e) {
            log.warn("ModelMapper failed for expense {}, using manual mapping: {}", expense.getId(), e.getMessage());
            
            ExpenseResponse response = new ExpenseResponse();
            response.setId(expense.getId());
            response.setDescription(expense.getDescription());
            response.setAmount(expense.getAmount());
            response.setCurrency(expense.getCurrency());
            response.setAmountInr(expense.getAmountInr());
            response.setExchangeRate(expense.getExchangeRate());
            response.setExpenseDate(expense.getExpenseDate());
            response.setStatus(expense.getStatus());
            response.setApprovalDate(expense.getApprovalDate());
            response.setRemarks(expense.getRemarks());
            response.setCreatedAt(expense.getCreatedAt());
            response.setUpdatedAt(expense.getUpdatedAt());
            
            // Map nested objects safely
            if (expense.getEmployee() != null) {
                try {
                    response.setEmployee(modelMapper.map(expense.getEmployee(), com.expense.management.dto.Employee.class));
                } catch (Exception ex) {
                    response.setEmployee(null);
                }
            }
            
            if (expense.getExpenseCategory() != null) {
                try {
                    response.setExpenseCategory(modelMapper.map(expense.getExpenseCategory(), com.expense.management.dto.ExpenseCategory.class));
                } catch (Exception ex) {
                    response.setExpenseCategory(null);
                }
            }
            
            // Skip approvedBy if it causes issues
            response.setApprovedBy(null);
            
            return response;
        }
    }
}