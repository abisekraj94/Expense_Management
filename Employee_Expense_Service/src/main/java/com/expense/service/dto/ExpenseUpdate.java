package com.expense.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for expense status updates
 * Used by admin to update expense status and reviewer information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUpdate {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Requested|Inprogress|Approved|Rejected|Reimbursed", 
             message = "Status must be one of: Requested, Inprogress, Approved, Rejected, Reimbursed")
    private String status;

    private Long reviewedBy;
}