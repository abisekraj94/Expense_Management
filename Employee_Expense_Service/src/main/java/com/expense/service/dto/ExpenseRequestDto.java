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
public class ExpenseRequestDto {

    @NotNull(message = "${app.validation.message.employee.id.required}")
    private Long employeeId;

    @NotNull(message = "${app.validation.message.category.id.required}")
    private Long expenseCategoryId;

    @NotBlank(message = "${app.validation.message.currency.required}")
    @Pattern(regexp = "${app.validation.currency.pattern}", message = "${app.validation.message.currency.invalid}")
    private String currency;

    @NotNull(message = "${app.validation.message.amount.required}")
    @DecimalMin(value = "${app.validation.amount.min}", message = "${app.validation.message.amount.min}")
    @Digits(integer = 8, fraction = 2, message = "${app.validation.message.amount.format}")
    private BigDecimal amount;

    @NotBlank(message = "${app.validation.message.description.required}")
    @Size(max = 500, message = "${app.validation.message.description.max}")
    private String description;

    @NotNull(message = "${app.validation.message.date.required}")
    @PastOrPresent(message = "${app.validation.message.date.future}")
    private LocalDate dateOfExpense;

    private List<String> documents;
}