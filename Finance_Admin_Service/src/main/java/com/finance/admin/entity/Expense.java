package com.finance.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expense entity representing expense records for approval workflow
 * Contains expense details and approval status information
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends BaseEntity {

    /**
     * Primary key for expense
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    /**
     * Employee who submitted the expense
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_expense_employee"))
    private Employee employee;

    /**
     * Description of the expense
     */
    @NotBlank(message = "${validation.description.required}")
    @Size(max = 500, message = "${validation.description.max.length}")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    /**
     * Expense amount
     */
    @NotNull(message = "${validation.amount.required}")
    @DecimalMin(value = "0.01", message = "${validation.amount.positive}")
    @Digits(integer = 10, fraction = 2, message = "${validation.amount.digits}")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * Currency of the expense
     */
    @NotBlank(message = "${validation.currency.required}")
    @Size(min = 3, max = 3, message = "${validation.currency.length}")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Date when the expense was incurred
     */
    @NotNull(message = "${validation.expense.date.required}")
    @PastOrPresent(message = "${validation.date.future}")
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    /**
     * Current status of the expense
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ExpenseStatus status = ExpenseStatus.PENDING;

    /**
     * Admin who approved/rejected the expense
     */
    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    /**
     * Date when the expense was approved/rejected
     */
    @Column(name = "approval_date")
    private LocalDate approvalDate;

    /**
     * Reason for rejection (if applicable)
     */
    @Size(max = 1000, message = "${validation.reason.max.length}")
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    /**
     * Amount converted to INR for reporting
     */
    @Column(name = "amount_inr", precision = 12, scale = 2)
    private BigDecimal amountInr;

    /**
     * Exchange rate used for conversion
     */
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate;
}