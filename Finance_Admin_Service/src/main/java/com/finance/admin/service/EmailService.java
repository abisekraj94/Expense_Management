package com.finance.admin.service;

import com.finance.admin.dto.Expense;
import com.finance.admin.exception.EmailException;

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
    void sendApprovalNotification(Expense expense) throws EmailException;

    /**
     * Send rejection notification email to employee
     * 
     * @param expense rejected expense details
     */
    void sendRejectionNotification(Expense expense) throws EmailException;
}