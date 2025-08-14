package com.expense.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing employee expense submissions
 * Contains expense details, status, and relationships
 */
@Entity
@Table(name = "employee_expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategory expenseCategory;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "amount_inr", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountInr;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "date_of_expense", nullable = false)
    private LocalDate dateOfExpense;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "employeeExpense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeExpenseDoc> documents;

    /**
     * JPA lifecycle method called before entity persistence
     * Sets creation and update timestamps
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA lifecycle method called before entity update
     * Updates the modification timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}