package com.expense.management.entity;

import com.expense.management.constants.ApplicationConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EmployeeExpense entity representing employee expense submissions
 * Maps to the employee_expense table in the database
 * 
 * @author System
 * @version 1.0.0
 */
@Entity
@Table(name = "employee_expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpense {

    /**
     * Primary key for employee expense
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Employee who submitted the expense
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", referencedColumnName = "id")
    private Employee employee;

    /**
     * Expense category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id", referencedColumnName = "id")
    private ExpenseCategory expenseCategory;

    /**
     * Expense description
     */
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    /**
     * Original expense amount
     */
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * Original currency code
     */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Date when expense was incurred
     */
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    /**
     * Current status of expense
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = ApplicationConstants.EXPENSE_STATUS_PENDING;

    /**
     * Date when expense was approved/rejected
     */
    @Column(name = "approval_date")
    private LocalDate approvalDate;

    /**
     * Reason for rejection (if applicable)
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;

    /**
     * Amount converted to INR
     */
    @Column(name = "amount_inr", precision = 12, scale = 2)
    private BigDecimal amountInr;

    /**
     * Exchange rate used for conversion
     */
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate;

    /**
     * Timestamp when expense was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when expense was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * User who created the expense
     */
    @Column(name = "created_by", nullable = false)
    private String createdBy = ApplicationConstants.SYSTEM_USER;

    /**
     * User who last updated the expense
     */
    @Column(name = "updated_by")
    private String updatedBy = ApplicationConstants.SYSTEM_USER;

    /**
     * Soft delete flag
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * Pre-persist callback to set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ApplicationConstants.EXPENSE_STATUS_PENDING;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    /**
     * Pre-update callback to set update timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}