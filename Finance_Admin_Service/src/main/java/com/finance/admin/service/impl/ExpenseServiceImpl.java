package com.finance.admin.service.impl;

import com.finance.admin.dto.*;
import com.finance.admin.entity.ExpenseStatus;
import com.finance.admin.exception.EmailException;
import com.finance.admin.exception.GlobalExceptionHandler.BusinessException;
import com.finance.admin.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.finance.admin.repository.EmployeeRepository;
import com.finance.admin.repository.ExpenseRepository;
import com.finance.admin.service.CurrencyService;
import com.finance.admin.service.EmailService;
import com.finance.admin.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService interface
 * Handles all expense management business logic
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrencyService currencyService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Value("${report.max.employees}")
    private int maxEmployeesForReport;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Expense> getPendingExpenses(Pageable pageable) {
        log.debug("Fetching pending expenses with pagination: {}", pageable);
        
        try {
            Page<com.finance.admin.entity.Expense> expensePage = expenseRepository
                    .findByStatusAndIsDeletedFalseOrderByCreatedAtAsc(ExpenseStatus.PENDING, pageable);
            
            List<Expense> expenseDtos = expensePage.getContent()
                    .stream()
                    .map(expense -> modelMapper.map(expense, Expense.class))
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} pending expenses", expenseDtos.size());
            return new PageImpl<>(expenseDtos, pageable, expensePage.getTotalElements());
            
        } catch (Exception e) {
            log.error("Error fetching pending expenses: {}", e.getMessage(), e);
            throw new BusinessException("Failed to fetch pending expenses", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense approveExpense(ApprovalRequest approvalRequest) {
        log.debug("Approving expense with ID: {}", approvalRequest.getExpenseId());
        
        try {
            com.finance.admin.entity.Expense expense = expenseRepository.findById(approvalRequest.getExpenseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Expense not found with ID: " + approvalRequest.getExpenseId()));
            
            if (expense.getStatus() != ExpenseStatus.PENDING) {
                throw new BusinessException("Expense is not in pending status");
            }
            
            // Update expense status
            expense.setStatus(ExpenseStatus.APPROVED);
            expense.setApprovedBy(approvalRequest.getApprovedBy());
            expense.setApprovalDate(LocalDate.now());
            expense.setUpdatedBy(approvalRequest.getApprovedBy());
            
            // Convert amount to INR if not already done
            if (expense.getAmountInr() == null) {
                convertAmountToInr(expense);
            }
            
            com.finance.admin.entity.Expense savedExpense = expenseRepository.save(expense);
            Expense expenseDto = modelMapper.map(savedExpense, Expense.class);
            
            // Send approval notification email asynchronously
            try {
                emailService.sendApprovalNotification(expenseDto);
            } catch (EmailException e) {
                log.warn("Failed to send approval notification for expense {}: {}", 
                        expense.getExpenseId(), e.getMessage());
                // Continue processing as email failure shouldn't fail the approval
            }
            
            log.info("Expense approved successfully: {}", expense.getExpenseId());
            return expenseDto;
            
        } catch (ResourceNotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error approving expense: {}", e.getMessage(), e);
            throw new BusinessException("Failed to approve expense", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense rejectExpense(RejectionRequest rejectionRequest) {
        log.debug("Rejecting expense with ID: {}", rejectionRequest.getExpenseId());
        
        try {
            com.finance.admin.entity.Expense expense = expenseRepository.findById(rejectionRequest.getExpenseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Expense not found with ID: " + rejectionRequest.getExpenseId()));
            
            if (expense.getStatus() != ExpenseStatus.PENDING) {
                throw new BusinessException("Expense is not in pending status");
            }
            
            // Update expense status
            expense.setStatus(ExpenseStatus.REJECTED);
            expense.setApprovedBy(rejectionRequest.getRejectedBy());
            expense.setApprovalDate(LocalDate.now());
            expense.setRejectionReason(rejectionRequest.getRejectionReason());
            expense.setUpdatedBy(rejectionRequest.getRejectedBy());
            
            com.finance.admin.entity.Expense savedExpense = expenseRepository.save(expense);
            Expense expenseDto = modelMapper.map(savedExpense, Expense.class);
            
            // Send rejection notification email asynchronously
            try {
                emailService.sendRejectionNotification(expenseDto);
            } catch (EmailException e) {
                log.warn("Failed to send rejection notification for expense {}: {}", 
                        expense.getExpenseId(), e.getMessage());
                // Continue processing as email failure shouldn't fail the rejection
            }
            
            log.info("Expense rejected successfully: {}", expense.getExpenseId());
            return expenseDto;
            
        } catch (ResourceNotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error rejecting expense: {}", e.getMessage(), e);
            throw new BusinessException("Failed to reject expense", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long expenseId) {
        log.debug("Fetching expense with ID: {}", expenseId);
        
        try {
            com.finance.admin.entity.Expense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Expense not found with ID: " + expenseId));
            
            return modelMapper.map(expense, Expense.class);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching expense: {}", e.getMessage(), e);
            throw new BusinessException("Failed to fetch expense", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseReport.CurrencyTotalDto> getTotalApprovedAmountByCurrency() {
        log.debug("Fetching total approved amount by currency");
        
        try {
            List<Object[]> results = expenseRepository.getTotalApprovedAmountByCurrency();
            
            return results.stream()
                    .map(result -> ExpenseReport.CurrencyTotalDto.builder()
                            .currency((String) result[0])
                            .totalAmount((BigDecimal) result[1])
                            .totalAmountInr((BigDecimal) result[2])
                            .expenseCount((Long) result[3])
                            .build())
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error fetching currency totals: {}", e.getMessage(), e);
            throw new BusinessException("Failed to fetch currency totals", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalApprovedAmountInr() {
        log.debug("Fetching total approved amount in INR");
        
        try {
            return expenseRepository.getTotalApprovedAmountInr();
        } catch (Exception e) {
            log.error("Error fetching total amount in INR: {}", e.getMessage(), e);
            throw new BusinessException("Failed to fetch total amount in INR", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseReport> generateExpenseReport(
            List<Long> employeeIds, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        
        log.debug("Generating expense report for employees: {}, date range: {} to {}", 
                employeeIds, startDate, endDate);
        
        try {
            // Validate employee count
            if (employeeIds != null && employeeIds.size() > maxEmployeesForReport) {
                throw new BusinessException(String.format("Maximum %d employees allowed for report", maxEmployeesForReport));
            }
            
            // Get employee totals
            List<Object[]> employeeTotals = expenseRepository
                    .getTotalApprovedAmountByEmployee(employeeIds, startDate, endDate);
            
            List<ExpenseReport> reports = employeeTotals.stream()
                    .map(result -> {
                        Long employeeId = (Long) result[0];
                        BigDecimal totalAmountInr = (BigDecimal) result[1];
                        Long expenseCount = (Long) result[2];
                        
                        // Get employee details
                        var employee = employeeRepository.findById(employeeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Employee not found with ID: " + employeeId));
                        
                        // Get currency breakdown
                        List<Object[]> currencyTotals = expenseRepository
                                .getCurrencyTotalsByEmployee(employeeId, startDate, endDate);
                        
                        List<ExpenseReport.CurrencyTotalDto> currencyDtos = currencyTotals.stream()
                                .map(ct -> ExpenseReport.CurrencyTotalDto.builder()
                                        .currency((String) ct[0])
                                        .totalAmount((BigDecimal) ct[1])
                                        .totalAmountInr((BigDecimal) ct[2])
                                        .expenseCount((Long) ct[3])
                                        .build())
                                .collect(Collectors.toList());
                        
                        return ExpenseReport.builder()
                                .employee(modelMapper.map(employee, Employee.class))
                                .totalApprovedAmountInr(totalAmountInr)
                                .currencyTotals(currencyDtos)
                                .approvedExpenseCount(expenseCount)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            log.info("Generated expense report for {} employees", reports.size());
            return new PageImpl<>(reports, pageable, reports.size());
            
        } catch (BusinessException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating expense report: {}", e.getMessage(), e);
            throw new BusinessException("Failed to generate expense report", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense syncExpenseFromEmployeeService(Long expenseId) {
        log.debug("Syncing expense from Employee Service: {}", expenseId);
        
        // This would typically call the Employee Service to get expense details
        // For now, we'll just return the existing expense
        return getExpenseById(expenseId);
    }

    /**
     * Convert expense amount to INR using current exchange rate
     * 
     * @param expense expense to convert
     */
    private void convertAmountToInr(com.finance.admin.entity.Expense expense) {
        try {
            if (!com.finance.admin.util.Constants.CURRENCY_INR.equals(expense.getCurrency())) {
                BigDecimal exchangeRate = currencyService.getExchangeRateToInr(expense.getCurrency());
                BigDecimal amountInr = expense.getAmount().multiply(exchangeRate);
                
                expense.setExchangeRate(exchangeRate);
                expense.setAmountInr(amountInr);
            } else {
                expense.setExchangeRate(BigDecimal.ONE);
                expense.setAmountInr(expense.getAmount());
            }
        } catch (Exception e) {
            log.warn("Failed to convert amount to INR for expense {}: {}", 
                    expense.getExpenseId(), e.getMessage());
            // Set default values if conversion fails
            expense.setExchangeRate(BigDecimal.ONE);
            expense.setAmountInr(expense.getAmount());
        }
    }
}