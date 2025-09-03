package com.expense.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for expense response data
 * Contains complete expense information for API responses
 * 
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    /**
     * Expense ID
     */
    private Integer id;

    /**
     * Employee information
     */
    private Employee employee;

    /**
     * Approver information (if approved/rejected)
     */
    private Employee approvedBy;

    /**
     * Expense category information
     */
    private ExpenseCategory expenseCategory;

    /**
     * Expense description
     */
    private String description;

    /**
     * Original expense amount
     */
    private BigDecimal amount;

    /**
     * Original currency
     */
    private String currency;

    /**
     * Amount converted to INR
     */
    private BigDecimal amountInr;

    /**
     * Exchange rate used for conversion
     */
    private BigDecimal exchangeRate;

    /**
     * Date when expense was incurred
     */
    private LocalDate expenseDate;

    /**
     * Current expense status
     */
    private String status;

    /**
     * Date when expense was approved/rejected
     */
    private LocalDate approvalDate;

    /**
     * Remarks reason (if approved/rejected)
     */
    private String remarks;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;
}