package com.expense.management.service;

import com.expense.management.dto.ExpenseReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Service interface for report generation operations
 * Defines contract for expense reporting functionality
 * 
 * @author System
 * @version 1.0.0
 */
public interface ReportService {

    /**
     * Generates expense report for a specific employee
     * 
     * @param empId employee ID
     * @param startDate start date for report
     * @param endDate end date for report
     * @return expense report for employee
     */
    ExpenseReport generateEmployeeExpenseReport(Long empId, LocalDate startDate, LocalDate endDate);

    /**
     * Generates expense report for employees with pagination
     * 
     * @param startDate start date for report
     * @param endDate end date for report
     * @param pageable pagination information
     * @return page of employee expense reports
     */
    Page<ExpenseReport> generateExpenseReportsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}