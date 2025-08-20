package com.finance.admin.service.impl;

import com.finance.admin.dto.Expense;
import com.finance.admin.exception.EmailException;
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
    
    @Value("${email.subject.approval.prefix}")
    private String approvalSubjectPrefix;
    
    @Value("${email.subject.rejection.prefix}")
    private String rejectionSubjectPrefix;
    
    @Value("${email.encoding}")
    private String emailEncoding;
    
    @Value("${email.template.var.expense}")
    private String expenseVar;
    
    @Value("${email.template.var.employee.name}")
    private String employeeNameVar;
    
    @Value("${email.template.var.amount}")
    private String amountVar;
    
    @Value("${email.template.var.currency}")
    private String currencyVar;
    
    @Value("${email.template.var.description}")
    private String descriptionVar;
    
    @Value("${email.template.var.approved.by}")
    private String approvedByVar;
    
    @Value("${email.template.var.approval.date}")
    private String approvalDateVar;
    
    @Value("${email.template.var.rejected.by}")
    private String rejectedByVar;
    
    @Value("${email.template.var.rejection.date}")
    private String rejectionDateVar;
    
    @Value("${email.template.var.rejection.reason}")
    private String rejectionReasonVar;
    
    @Value("${log.sending.approval.notification}")
    private String logSendingApprovalNotification;
    
    @Value("${log.sending.rejection.notification}")
    private String logSendingRejectionNotification;
    
    @Value("${success.approval.notification.sent}")
    private String successApprovalNotificationSent;
    
    @Value("${success.rejection.notification.sent}")
    private String successRejectionNotificationSent;
    
    @Value("${success.email.sent}")
    private String successEmailSent;
    
    @Value("${error.failed.send.approval.notification}")
    private String errorFailedSendApprovalNotification;
    
    @Value("${error.failed.send.rejection.notification}")
    private String errorFailedSendRejectionNotification;
    
    @Value("${error.failed.send.email}")
    private String errorFailedSendEmail;

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public void sendApprovalNotification(Expense expense) throws EmailException {
        log.debug(logSendingApprovalNotification + "{}", expense.getExpenseId());
        
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable(expenseVar, expense);
            context.setVariable(employeeNameVar, expense.getEmployee().getEmployeeName());
            context.setVariable(amountVar, expense.getAmount());
            context.setVariable(currencyVar, expense.getCurrency());
            context.setVariable(descriptionVar, expense.getDescription());
            context.setVariable(approvedByVar, expense.getApprovedBy());
            context.setVariable(approvalDateVar, expense.getApprovalDate());
            
            String htmlContent = templateEngine.process(approvalTemplate, context);
            
            sendEmail(
                expense.getEmployee().getEmail(),
                approvalSubjectPrefix + expense.getDescription(),
                htmlContent
            );
            
            log.info(successApprovalNotificationSent + "{}", expense.getExpenseId());
            
        } catch (EmailException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send approval notification for expense {}: {}", 
                    expense.getExpenseId(), e.getMessage(), e);
            throw new EmailException(errorFailedSendApprovalNotification, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public void sendRejectionNotification(Expense expense) throws EmailException {
        log.debug(logSendingRejectionNotification + "{}", expense.getExpenseId());
        
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable(expenseVar, expense);
            context.setVariable(employeeNameVar, expense.getEmployee().getEmployeeName());
            context.setVariable(amountVar, expense.getAmount());
            context.setVariable(currencyVar, expense.getCurrency());
            context.setVariable(descriptionVar, expense.getDescription());
            context.setVariable(rejectedByVar, expense.getApprovedBy());
            context.setVariable(rejectionDateVar, expense.getApprovalDate());
            context.setVariable(rejectionReasonVar, expense.getRejectionReason());
            
            String htmlContent = templateEngine.process(rejectionTemplate, context);
            
            sendEmail(
                expense.getEmployee().getEmail(),
                rejectionSubjectPrefix + expense.getDescription(),
                htmlContent
            );
            
            log.info(successRejectionNotificationSent + "{}", expense.getExpenseId());
            
        } catch (EmailException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send rejection notification for expense {}: {}", 
                    expense.getExpenseId(), e.getMessage(), e);
            throw new EmailException(errorFailedSendRejectionNotification, e);
        }
    }

    /**
     * Send HTML email
     * 
     * @param to      recipient email
     * @param subject email subject
     * @param content HTML content
     */
    private void sendEmail(String to, String subject, String content) throws EmailException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, emailEncoding);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.debug(successEmailSent + "{}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new EmailException(errorFailedSendEmail, e);
        }
    }
}