package com.expense.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for expense reports
 * Contains aggregated expense data for reporting purposes
 * 
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReport {

    /**
     * Employee information
     */
    private Employee employee;

    /**
     * Total approved expenses by currency
     */
    private List<CurrencyTotalDto> totalsByCurrency;

    /**
     * Total approved amount in INR
     */
    private BigDecimal totalAmountInr;

    /**
     * Number of approved expenses
     */
    private Long approvedExpenseCount;

    /**
     * DTO for currency-wise totals
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyTotalDto {
        private String currency;
        private BigDecimal totalAmount;
        private BigDecimal totalAmountInr;
        private Long expenseCount;
    }
}