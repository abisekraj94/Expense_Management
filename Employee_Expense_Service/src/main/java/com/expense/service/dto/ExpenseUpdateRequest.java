package com.expense.service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUpdateRequest {

    private Long expenseCategoryId;

    @Pattern(regexp = "USD|EUR|INR", message = "Currency must be USD, EUR, or INR")
    private String currency;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @PastOrPresent(message = "Date of expense cannot be in the future")
    private LocalDate dateOfExpense;

    private List<String> documents;

    private String status;
}