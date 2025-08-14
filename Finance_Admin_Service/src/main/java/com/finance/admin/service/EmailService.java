package com.finance.admin.service;

import com.finance.admin.dto.ExpenseDto;

/**
 * Service interface for email notification operations
 * Handles sending email notifications for expense approvals/rejections
 * 
 * @author Finance Team
 * @version 1.0.0
 */
public interface EmailService {

    /**
     * Send approval notification email to employee
     * 
     * @param expense approved expense details
     */
    void sendApprovalNotification(ExpenseDto expense);

    /**
     * Send rejection notification email to employee
     * 
     * @param expense rejected expense details
     */
    void sendRejectionNotification(ExpenseDto expense);
}