package com.finance.admin.service;

import com.finance.admin.dto.Employee;
import com.finance.admin.dto.Expense;
import com.finance.admin.entity.ExpenseStatus;
import com.finance.admin.exception.EmailException;
import com.finance.admin.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Expense testExpense;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        // Set up properties using ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "fromEmail", "finance@company.com");
        ReflectionTestUtils.setField(emailService, "approvalTemplate", "approval-notification");
        ReflectionTestUtils.setField(emailService, "rejectionTemplate", "rejection-notification");
        ReflectionTestUtils.setField(emailService, "approvalSubjectPrefix", "Expense Approved - ");
        ReflectionTestUtils.setField(emailService, "rejectionSubjectPrefix", "Expense Rejected - ");
        ReflectionTestUtils.setField(emailService, "emailEncoding", "UTF-8");
        ReflectionTestUtils.setField(emailService, "expenseVar", "expense");
        ReflectionTestUtils.setField(emailService, "employeeNameVar", "employeeName");
        ReflectionTestUtils.setField(emailService, "amountVar", "amount");
        ReflectionTestUtils.setField(emailService, "currencyVar", "currency");
        ReflectionTestUtils.setField(emailService, "descriptionVar", "description");
        ReflectionTestUtils.setField(emailService, "approvedByVar", "approvedBy");
        ReflectionTestUtils.setField(emailService, "approvalDateVar", "approvalDate");
        ReflectionTestUtils.setField(emailService, "rejectedByVar", "rejectedBy");
        ReflectionTestUtils.setField(emailService, "rejectionDateVar", "rejectionDate");
        ReflectionTestUtils.setField(emailService, "rejectionReasonVar", "rejectionReason");
        ReflectionTestUtils.setField(emailService, "logSendingApprovalNotification", "Sending approval notification for expense: ");
        ReflectionTestUtils.setField(emailService, "logSendingRejectionNotification", "Sending rejection notification for expense: ");
        ReflectionTestUtils.setField(emailService, "successApprovalNotificationSent", "Approval notification sent for expense: ");
        ReflectionTestUtils.setField(emailService, "successRejectionNotificationSent", "Rejection notification sent for expense: ");
        ReflectionTestUtils.setField(emailService, "successEmailSent", "Email sent successfully to: ");
        ReflectionTestUtils.setField(emailService, "errorFailedSendApprovalNotification", "Failed to send approval notification");
        ReflectionTestUtils.setField(emailService, "errorFailedSendRejectionNotification", "Failed to send rejection notification");
        ReflectionTestUtils.setField(emailService, "errorFailedSendEmail", "Failed to send email");

        testEmployee = Employee.builder()
                .employeeId(1L)
                .employeeName("John Doe")
                .email("john.doe@company.com")
                .department("IT")
                .build();

        testExpense = Expense.builder()
                .expenseId(1L)
                .employee(testEmployee)
                .description("Business Travel")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .expenseDate(LocalDate.now().minusDays(1))
                .status(ExpenseStatus.APPROVED)
                .approvedBy("admin")
                .approvalDate(LocalDate.now())
                .build();
    }

    @Test
    void sendApprovalNotification_ShouldSendEmailSuccessfully() throws EmailException, MessagingException {
        // Arrange
        String htmlContent = "<html>Approval notification</html>";
        
        when(templateEngine.process(eq("approval-notification"), any(Context.class)))
                .thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendApprovalNotification(testExpense));

        // Assert
        verify(templateEngine).process(eq("approval-notification"), any(Context.class));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendApprovalNotification_ShouldThrowEmailException_WhenTemplateProcessingFails() {
        // Arrange
        when(templateEngine.process(eq("approval-notification"), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        EmailException exception = assertThrows(EmailException.class,
                () -> emailService.sendApprovalNotification(testExpense));
        
        assertEquals("Failed to send approval notification", exception.getMessage());
        verify(templateEngine).process(eq("approval-notification"), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendRejectionNotification_ShouldSendEmailSuccessfully() throws EmailException, MessagingException {
        // Arrange
        testExpense.setStatus(ExpenseStatus.REJECTED);
        testExpense.setRejectionReason("Invalid receipt");
        String htmlContent = "<html>Rejection notification</html>";
        
        when(templateEngine.process(eq("rejection-notification"), any(Context.class)))
                .thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendRejectionNotification(testExpense));

        // Assert
        verify(templateEngine).process(eq("rejection-notification"), any(Context.class));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendRejectionNotification_ShouldThrowEmailException_WhenMailSendingFails() throws MessagingException {
        // Arrange
        testExpense.setStatus(ExpenseStatus.REJECTED);
        testExpense.setRejectionReason("Invalid receipt");
        String htmlContent = "<html>Rejection notification</html>";
        
        when(templateEngine.process(eq("rejection-notification"), any(Context.class)))
                .thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server unavailable")).when(mailSender).send(mimeMessage);

        // Act & Assert
        EmailException exception = assertThrows(EmailException.class,
                () -> emailService.sendRejectionNotification(testExpense));
        
        assertEquals("Failed to send rejection notification", exception.getMessage());
        verify(templateEngine).process(eq("rejection-notification"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }
}