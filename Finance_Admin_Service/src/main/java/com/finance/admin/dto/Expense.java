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
public class Expense {

    private Long expenseId;

    private Employee employee;

    private String description;

    private BigDecimal amount;

    private String currency;

    private LocalDate expenseDate;

    private ExpenseStatus status;

    private String approvedBy;

    private LocalDate approvalDate;

    private String rejectionReason;

    private BigDecimal amountInr;

    private BigDecimal exchangeRate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}