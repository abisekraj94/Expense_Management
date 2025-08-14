package com.finance.admin.entity;

/**
 * Enumeration for expense approval status
 * Defines the possible states of an expense in the approval workflow
 * 
 * @author Finance Team
 * @version 1.0.0
 */
public enum ExpenseStatus {
    /**
     * Expense is pending approval from finance admin
     */
    PENDING,
    
    /**
     * Expense has been approved by finance admin
     */
    APPROVED,
    
    /**
     * Expense has been rejected by finance admin
     */
    REJECTED
}