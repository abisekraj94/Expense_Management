package com.expense.management.dto;

import com.expense.management.constants.ApplicationConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for expense submission requests
 * Contains validation rules for expense submission data
 * 
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSubmission {

    /**
     * Employee ID submitting the expense
     */
    @NotNull(message = "${app.constants.validation.messages.employee-id.required}")
    @Positive(message = "${app.constants.validation.messages.employee-id.positive}")
    private Long empId;

    /**
     * Expense category ID
     */
    @NotNull(message = "${app.constants.validation.messages.expense-category-id.required}")
    @Positive(message = "${app.constants.validation.messages.expense-category-id.positive}")
    private Integer expenseCategoryId;

    /**
     * Expense description
     */
    @NotBlank(message = "${app.constants.validation.messages.description.required}")
    @Size(max = ApplicationConstants.DESCRIPTION_MAX_LENGTH, message = "${app.constants.validation.messages.description.max-length}")
    private String description;

    /**
     * Expense amount
     */
    @NotNull(message = "${app.constants.validation.messages.amount.required}")
    @DecimalMin(value = "0.01", message = "${app.constants.validation.messages.amount.min}")
    @Digits(integer = ApplicationConstants.AMOUNT_MAX_DIGITS, fraction = ApplicationConstants.AMOUNT_MAX_FRACTION, message = "${app.constants.validation.messages.amount.format}")
    private BigDecimal amount;

    /**
     * Currency code
     */
    @NotBlank(message = "${app.constants.validation.messages.currency.required}")
    @Size(min = ApplicationConstants.CURRENCY_LENGTH, max = ApplicationConstants.CURRENCY_LENGTH, message = "${app.constants.validation.messages.currency.length}")
    @Pattern(regexp = ApplicationConstants.CURRENCY_PATTERN, message = "${app.constants.validation.messages.currency.format}")
    private String currency;

    /**
     * Date when expense was incurred
     */
    @NotNull(message = "${app.constants.validation.messages.expense-date.required}")
    @PastOrPresent(message = "${app.constants.validation.messages.expense-date.past}")
    private LocalDate expenseDate;
}