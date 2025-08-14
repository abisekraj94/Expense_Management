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
public class ExpenseUpdateDto {

    @NotBlank(message = "${app.validation.message.status.required}")
    @Pattern(regexp = "${app.validation.status.pattern}", 
             message = "${app.validation.message.status.invalid}")
    private String status;

    private Long reviewedBy;
}