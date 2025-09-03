package com.expense.management.service.impl;

import com.expense.management.dto.Employee;
import com.expense.management.dto.ExpenseReport;
import com.expense.management.entity.EmployeeExpense;
import com.expense.management.exception.ResourceNotFoundException;
import com.expense.management.repository.EmployeeExpenseRepository;
import com.expense.management.repository.EmployeeRepository;
import com.expense.management.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ReportService interface
 * Handles expense report generation operations
 * 
 * @author System
 * @version 1.0.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final EmployeeExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructor for ReportServiceImpl
     */
    public ReportServiceImpl(EmployeeExpenseRepository expenseRepository,
                             EmployeeRepository employeeRepository,
                             ModelMapper modelMapper) {
        this.expenseRepository = expenseRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Generates expense report for a specific employee
     * 
     * @param empId employee ID
     * @param startDate start date for report
     * @param endDate end date for report
     * @return expense report for employee
     */
    @Override
    public ExpenseReport generateEmployeeExpenseReport(Long empId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating expense report for employee ID: {} from {} to {}", empId, startDate, endDate);

        try {
            // Validate employee exists
            com.expense.management.entity.Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));

            // Get approved expenses for the employee within date range
            List<EmployeeExpense> approvedExpenses = expenseRepository
                    .findApprovedExpensesByEmployeeAndDateRange(empId, startDate, endDate);

            // Group expenses by currency and calculate totals
            Map<String, List<EmployeeExpense>> expensesByCurrency = approvedExpenses.stream()
                    .collect(Collectors.groupingBy(EmployeeExpense::getCurrency));

            List<ExpenseReport.CurrencyTotalDto> currencyTotals = expensesByCurrency.entrySet().stream()
                    .map(entry -> {
                        String currency = entry.getKey();
                        List<EmployeeExpense> expenses = entry.getValue();
                        
                        BigDecimal totalAmount = expenses.stream()
                                .map(EmployeeExpense::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        BigDecimal totalAmountInr = expenses.stream()
                                .map(EmployeeExpense::getAmountInr)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        return new ExpenseReport.CurrencyTotalDto(
                                currency, totalAmount, totalAmountInr, (long) expenses.size());
                    })
                    .collect(Collectors.toList());

            // Calculate total INR amount
            BigDecimal totalAmountInr = expenseRepository.getTotalApprovedAmountInrForEmployee(empId);

            // Create report DTO
            ExpenseReport report = new ExpenseReport();
            report.setEmployee(modelMapper.map(employee, Employee.class));
            report.setTotalsByCurrency(currencyTotals);
            report.setTotalAmountInr(totalAmountInr);
            report.setApprovedExpenseCount((long) approvedExpenses.size());

            log.info("Generated expense report for employee ID: {} with {} approved expenses", 
                    empId, approvedExpenses.size());

            return report;

        } catch (Exception e) {
            log.error("Error generating expense report for employee {}: {}", empId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Generates expense report for employees with pagination
     * 
     * @param startDate start date for report
     * @param endDate end date for report
     * @param pageable pagination information
     * @return page of employee expense reports
     */
    @Override
    public Page<ExpenseReport> generateExpenseReportsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("Generating expense reports from {} to {} with pagination", startDate, endDate);

        try {
            // Get employees who have expenses in the date range
            List<Long> employeeIds = expenseRepository.findByExpenseDateBetweenAndIsDeletedFalse(startDate, endDate, Pageable.unpaged())
                    .getContent().stream()
                    .map(expense -> expense.getEmployee().getId())
                    .distinct()
                    .collect(Collectors.toList());

            // Apply pagination to employee IDs
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), employeeIds.size());
            List<Long> paginatedEmployeeIds = employeeIds.subList(start, end);
            
            // Generate report for each employee
            List<ExpenseReport> reports = paginatedEmployeeIds.stream()
                    .map(empId -> generateEmployeeExpenseReport(empId, startDate, endDate))
                    .collect(Collectors.toList());

            log.info("Generated {} expense reports for date range {} to {}", 
                    reports.size(), startDate, endDate);

            return new PageImpl<>(reports, pageable, employeeIds.size());

        } catch (Exception e) {
            log.error("Error generating expense reports by date range: {}", e.getMessage(), e);
            throw e;
        }
    }
}