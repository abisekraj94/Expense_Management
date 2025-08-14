package com.finance.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Data Transfer Object for expense approval requests
 * Used when approving expenses
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDto {

    /**
     * Expense ID to approve
     */
    @NotNull(message = "${validation.expense.id.required}")
    private Long expenseId;

    /**
     * Admin performing the approval
     */
    private String approvedBy;
}