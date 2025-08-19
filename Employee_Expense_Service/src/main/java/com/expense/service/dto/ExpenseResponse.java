package com.expense.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for expense response data
 * Contains complete expense information for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private Long employeeId;
    private String categoryName;
    private String currency;
    private BigDecimal amount;
    private BigDecimal amountInr;
    private String description;
    private LocalDate dateOfExpense;
    private String status;
    private Long reviewedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> documents;
}