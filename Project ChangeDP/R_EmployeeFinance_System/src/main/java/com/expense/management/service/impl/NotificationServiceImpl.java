package com.expense.management.service.impl;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.entity.EmployeeExpense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementation of NotificationService interface
 * Handles email notifications using Thymeleaf templates
 * 
 * @author System
 * @version 1.0.0
 */
@Service
@Slf4j
public class NotificationServiceImpl extends NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Constructor for NotificationServiceImpl
     * 
     * @param mailSender JavaMailSender for sending emails
     * @param templateEngine Thymeleaf template engine
     */
    public NotificationServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Sends expense status notification email asynchronously
     * 
     * @param expense expense with updated status
     */
    @Async
    public void sendExpenseStatusNotification(EmployeeExpense expense) {
        log.info("Sending expense status notification for expense ID: {} to employee: {}", 
                expense.getId(), expense.getEmployee().getEmailId());

        try {
            String templateName;
            String subject;
            
            if (ApplicationConstants.EXPENSE_STATUS_APPROVED.equals(expense.getStatus())) {
                templateName = ApplicationConstants.EMAIL_TEMPLATE_APPROVAL;
                subject = ApplicationConstants.EMAIL_SUBJECT_APPROVAL + expense.getId();
            } else if (ApplicationConstants.EXPENSE_STATUS_REJECTED.equals(expense.getStatus())) {
                templateName = ApplicationConstants.EMAIL_TEMPLATE_REJECTION;
                subject = ApplicationConstants.EMAIL_SUBJECT_REJECTION + expense.getId();
            } else {
                log.warn("No email template configured for status: {}", expense.getStatus());
                return;
            }

            // Prepare email context
            Context context = new Context();
            context.setVariable("employee", expense.getEmployee());
            context.setVariable("expense", expense);

            // Process template
            String emailContent = templateEngine.process(templateName, context);

            // Send email
            sendEmail(expense.getEmployee().getEmailId(), subject, emailContent);
            
            log.info("Expense status notification sent successfully for expense ID: {}", expense.getId());

        } catch (Exception e) {
            log.error("Failed to send expense status notification for expense ID: {}: {}", 
                    expense.getId(), e.getMessage(), e);
        }
    }

    /**
     * Sends HTML email
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param content HTML email content
     * @throws MessagingException if email sending fails
     */
    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        
        mailSender.send(message);
        log.info("Email sent to: {} with subject: {}", to, subject);
    }
}