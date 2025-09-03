package com.expense.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for expense approval/rejection requests
 * Contains validation rules for approval/rejection actions
 * 
 * @author System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseApproval {

    /**
     * Approval action (APPROVED or REJECTED)
     */
    @NotNull(message = "${app.constants.validation.messages.action.required}")
    private String action;

    /**
     * Rejection reason (required if action is REJECTED)
     */
    @Size(max = 1000, message = "${app.constants.validation.messages.rejection-reason.max-length}")
    private String remarks;

}