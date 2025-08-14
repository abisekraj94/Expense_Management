package com.finance.admin.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for expense reports
 * Contains aggregated expense data for reporting
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseReportDto {

    /**
     * Employee information
     */
    private EmployeeDto employee;

    /**
     * Total approved amount by employee
     */
    private BigDecimal totalApprovedAmount;

    /**
     * Total approved amount in INR
     */
    private BigDecimal totalApprovedAmountInr;

    /**
     * Currency-wise breakdown
     */
    private List<CurrencyTotalDto> currencyTotals;

    /**
     * Number of approved expenses
     */
    private Long approvedExpenseCount;

    /**
     * Data Transfer Object for currency totals
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrencyTotalDto {
        
        /**
         * Currency code
         */
        private String currency;
        
        /**
         * Total amount in original currency
         */
        private BigDecimal totalAmount;
        
        /**
         * Total amount converted to INR
         */
        private BigDecimal totalAmountInr;
        
        /**
         * Number of expenses in this currency
         */
        private Long expenseCount;
    }
}