package com.finance.admin.service.impl;

import com.finance.admin.dto.Expense;
import com.finance.admin.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

/**
 * Implementation of EmailService interface
 * Handles email notifications using Thymeleaf templates
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.template.approval}")
    private String approvalTemplate;

    @Value("${email.template.rejection}")
    private String rejectionTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public void sendApprovalNotification(Expense expense) {
        log.debug("Sending approval notification for expense: {}", expense.getExpenseId());
        
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable("expense", expense);
            context.setVariable("employeeName", expense.getEmployee().getEmployeeName());
            context.setVariable("amount", expense.getAmount());
            context.setVariable("currency", expense.getCurrency());
            context.setVariable("description", expense.getDescription());
            context.setVariable("approvedBy", expense.getApprovedBy());
            context.setVariable("approvalDate", expense.getApprovalDate());
            
            String htmlContent = templateEngine.process(approvalTemplate, context);
            
            sendEmail(
                expense.getEmployee().getEmail(),
                "Expense Approved - " + expense.getDescription(),
                htmlContent
            );
            
            log.info("Approval notification sent for expense: {}", expense.getExpenseId());
            
        } catch (Exception e) {
            log.error("Failed to send approval notification for expense {}: {}", 
                    expense.getExpenseId(), e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public void sendRejectionNotification(Expense expense) {
        log.debug("Sending rejection notification for expense: {}", expense.getExpenseId());
        
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable("expense", expense);
            context.setVariable("employeeName", expense.getEmployee().getEmployeeName());
            context.setVariable("amount", expense.getAmount());
            context.setVariable("currency", expense.getCurrency());
            context.setVariable("description", expense.getDescription());
            context.setVariable("rejectedBy", expense.getApprovedBy());
            context.setVariable("rejectionDate", expense.getApprovalDate());
            context.setVariable("rejectionReason", expense.getRejectionReason());
            
            String htmlContent = templateEngine.process(rejectionTemplate, context);
            
            sendEmail(
                expense.getEmployee().getEmail(),
                "Expense Rejected - " + expense.getDescription(),
                htmlContent
            );
            
            log.info("Rejection notification sent for expense: {}", expense.getExpenseId());
            
        } catch (Exception e) {
            log.error("Failed to send rejection notification for expense {}: {}", 
                    expense.getExpenseId(), e.getMessage(), e);
        }
    }

    /**
     * Send HTML email
     * 
     * @param to      recipient email
     * @param subject email subject
     * @param content HTML content
     */
    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.debug("Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}