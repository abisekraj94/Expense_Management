package com.expense.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EmployeeExpenseDocs entity representing expense documents
 * Maps to the employee_expense_docs table in the database
 * Todo
 * @author System
 * @version 1.0.0
 */
@Entity
@Table(name = "employee_expense_docs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpenseDocs {

    /**
     * Primary key for expense document
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Associated employee expense
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_expense_id", referencedColumnName = "id", nullable = false)
    private EmployeeExpense employeeExpense;

    /**
     * Document file path or URL
     */
    @Column(name = "documents", nullable = false, length = 1000)
    private String documents;

    /**
     * Flag indicating if document is active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Timestamp when document was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when document was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Todo
     * Pre-persist callback to set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
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