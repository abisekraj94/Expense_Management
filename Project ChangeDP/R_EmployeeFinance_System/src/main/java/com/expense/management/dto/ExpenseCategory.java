package com.expense.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for expense category information
 * Contains expense category details for API responses
 * 
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategory {

    /**
     * Category ID
     */
    private Integer id;

    /**
     * Category name
     */
    private String category;

    /**
     * Maximum limit for this category
     */
    private BigDecimal maxLimit;

    /**
     * Active status
     */
    private Boolean isActive;
}