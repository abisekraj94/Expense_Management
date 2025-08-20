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
public class ExpenseReport {

    private Employee employee;

    private BigDecimal totalApprovedAmount;

    private BigDecimal totalApprovedAmountInr;

    private List<CurrencyTotalDto> currencyTotals;

    private Long approvedExpenseCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrencyTotalDto {
        
        private String currency;
        
        private BigDecimal totalAmount;
        
        private BigDecimal totalAmountInr;
        
        private Long expenseCount;
    }
}