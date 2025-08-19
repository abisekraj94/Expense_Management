package com.expense.service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for expense creation and update requests
 * Contains validation rules for expense submission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Expense category ID is required")
    private Long expenseCategoryId;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "USD|EUR|INR", message = "Currency must be USD, EUR, or INR")
    private String currency;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Date of expense is required")
    @PastOrPresent(message = "Date of expense cannot be in the future")
    private LocalDate dateOfExpense;

    private List<String> documents;
}