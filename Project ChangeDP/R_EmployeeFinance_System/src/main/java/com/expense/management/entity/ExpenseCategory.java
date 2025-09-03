package com.expense.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ExpenseCategory entity representing expense categories with limits
 * Maps to the expense_category table in the database
 * 
 * @author System
 * @version 1.0.0
 */
@Entity
@Table(name = "expense_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategory {

    /**
     * Primary key for expense category
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Category name (unique)
     */
    @Column(name = "category", nullable = false, unique = true, length = 50)
    private String category;

    /**
     * Maximum limit allowed for this category
     */
    @Column(name = "max_limit", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxLimit;

    /**
     * Flag indicating if category is active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Timestamp when category was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when category was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
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