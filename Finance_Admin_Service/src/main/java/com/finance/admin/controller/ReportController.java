package com.finance.admin.controller;

import com.finance.admin.dto.ExpenseReportDto;
import com.finance.admin.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for report generation operations
 * Handles expense reporting and analytics
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ExpenseService expenseService;

    /**
     * Generate expense report by employee with filters
     * 
     * @param employeeIds list of employee IDs (max 5)
     * @param startDate   start date for filtering
     * @param endDate     end date for filtering
     * @param pageable    pagination parameters
     * @return page of expense reports
     */
    @GetMapping("/expenses")
    public ResponseEntity<Page<ExpenseReportDto>> generateExpenseReport(
            @RequestParam(required = false) List<Long> employeeIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 5) Pageable pageable) {
        
        log.info("Generating expense report for employees: {}, date range: {} to {}", 
                employeeIds, startDate, endDate);
        
        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Page<ExpenseReportDto> reports = expenseService.generateExpenseReport(
                employeeIds, startDate, endDate, pageable);
        
        log.info("Generated expense report with {} entries", reports.getNumberOfElements());
        return ResponseEntity.ok(reports);
    }

    /**
     * Generate expense report by employee ID with date range
     * 
     * @param employeeId employee identifier
     * @param startDate  start date for filtering
     * @param endDate    end date for filtering
     * @param pageable   pagination parameters
     * @return page of expense reports
     */
    @GetMapping("/expenses/employee/{employeeId}")
    public ResponseEntity<Page<ExpenseReportDto>> generateExpenseReportByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 1) Pageable pageable) {
        
        log.info("Generating expense report for employee: {}, date range: {} to {}", 
                employeeId, startDate, endDate);
        
        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Page<ExpenseReportDto> reports = expenseService.generateExpenseReport(
                List.of(employeeId), startDate, endDate, pageable);
        
        log.info("Generated expense report for employee {} with {} entries", 
                employeeId, reports.getNumberOfElements());
        return ResponseEntity.ok(reports);
    }
}