package com.expense.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryRequest {
    
    @NotBlank(message = "Category name is required")
    private String name;
    
    @NotNull(message = "Spending limit is required")
    @Positive(message = "Spending limit must be positive")
    private BigDecimal spendingLimit;
}