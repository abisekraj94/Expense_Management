package com.finance.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object for expense rejection requests
 * Used when rejecting expenses with reason
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectionRequest {

    @NotNull(message = "${validation.expense.id.required}")
    private Long expenseId;

    @NotBlank(message = "${validation.reason.required}")
    @Size(max = 1000, message = "${validation.reason.max.length}")
    private String rejectionReason;

    private String rejectedBy;
}