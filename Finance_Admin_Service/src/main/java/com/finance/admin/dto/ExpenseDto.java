package com.finance.admin.dto;

import com.finance.admin.entity.ExpenseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Expense entity
 * Used for API requests and responses
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDto {

    /**
     * Expense identifier
     */
    private Long expenseId;

    /**
     * Employee information
     */
    private EmployeeDto employee;

    /**
     * Expense description
     */
    private String description;

    /**
     * Expense amount
     */
    private BigDecimal amount;

    /**
     * Currency code
     */
    private String currency;

    /**
     * Date of expense
     */
    private LocalDate expenseDate;

    /**
     * Current status
     */
    private ExpenseStatus status;

    /**
     * Admin who approved/rejected
     */
    private String approvedBy;

    /**
     * Approval date
     */
    private LocalDate approvalDate;

    /**
     * Rejection reason
     */
    private String rejectionReason;

    /**
     * Amount in INR
     */
    private BigDecimal amountInr;

    /**
     * Exchange rate used
     */
    private BigDecimal exchangeRate;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;
}