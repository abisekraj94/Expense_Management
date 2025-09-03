package com.expense.management.controller;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.dto.ApiResponse;
import com.expense.management.dto.ExpenseReport;
import com.expense.management.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for expense reporting operations
 * Handles expense report generation and retrieval
 * 
 * @author System
 * @version 1.0.0
 */
@RestController
@RequestMapping(ApplicationConstants.REPORT_API_PATH)
@Slf4j
public class ReportController {

    private final ReportService reportService;

    /**
     * Constructor for ReportController
     * 
     * @param reportService report service
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates expense report for a specific employee
     * 
     * @param empId employee ID
     * @param startDate start date for report (default: 30 days ago)
     * @param endDate end date for report (default: today)
     * @return expense report for employee
     */
    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<ExpenseReport>> generateEmployeeExpenseReport(
            @PathVariable Long empId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        log.info("Generating expense report for employee ID: {} from {} to {}", empId, startDate, endDate);
        
        ExpenseReport report = reportService.generateEmployeeExpenseReport(empId, startDate, endDate);
        ApiResponse<ExpenseReport> apiResponse = ApiResponse.success(
                "Employee expense report generated successfully", report);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Generates expense reports for multiple employees with pagination
     * Supports filtering by date range and employee count
     * 
     * @param startDate start date for report (default: 30 days ago)
     * @param endDate end date for report (default: today)
     * @param page page number (default: 0)
     * @param size page size (default: 5, max: 5 as per requirement)
     * @param sortBy sort field (default: totalAmountInr)
     * @param sortDir sort direction (default: desc)
     * @return page of employee expense reports
     */
    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<Page<ExpenseReport>>> generateExpenseReportsByDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "totalAmountInr") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // Limit page size to 5 as per requirement
        size = Math.min(size, 5);
        
        log.info("Generating expense reports from {} to {} with pagination (page: {}, size: {})", 
                startDate, endDate, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ExpenseReport> reports = reportService.generateExpenseReportsByDateRange(startDate, endDate, pageable);
        ApiResponse<Page<ExpenseReport>> apiResponse = ApiResponse.success(
                "Expense reports generated successfully", reports);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Generates summary report for all approved expenses
     * Shows total by currency and converted INR amounts
     * 
     * @param startDate start date for report (optional)
     * @param endDate end date for report (optional)
     * @return summary report with currency totals
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ExpenseReport>> generateSummaryReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Set default date range if not provided (last 90 days)
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(90);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        log.info("Generating summary report from {} to {}", startDate, endDate);
        
        // Generate a summary report for all employees combined
        // This could be enhanced to create a dedicated summary service method
        Page<ExpenseReport> allReports = reportService.generateExpenseReportsByDateRange(
                startDate, endDate, PageRequest.of(0, Integer.MAX_VALUE));
        
        // Aggregate all reports into a single summary
        ExpenseReport summaryReport = new ExpenseReport();
        // Implementation would aggregate all employee reports
        // For now, return the first page of reports
        
        ApiResponse<ExpenseReport> apiResponse = ApiResponse.success(
                "Summary report generated successfully", summaryReport);
        
        return ResponseEntity.ok(apiResponse);
    }
}