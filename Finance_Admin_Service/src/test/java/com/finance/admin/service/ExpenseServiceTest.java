package com.finance.admin.service;

import com.finance.admin.dto.ApprovalRequest;
import com.finance.admin.dto.Expense;
import com.finance.admin.dto.ExpenseReport;
import com.finance.admin.dto.RejectionRequest;
import com.finance.admin.entity.Employee;
import com.finance.admin.entity.ExpenseStatus;
import com.finance.admin.exception.EmailException;
import com.finance.admin.exception.GlobalExceptionHandler.BusinessException;
import com.finance.admin.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.finance.admin.repository.EmployeeRepository;
import com.finance.admin.repository.ExpenseRepository;
import com.finance.admin.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseService
 * Tests expense management business logic
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private EmailService emailService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private com.finance.admin.entity.Expense testExpense;
    private Expense testExpenseDto;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(expenseService, "maxEmployeesForReport", 5);
        
        testEmployee = Employee.builder()
                .employeeId(1L)
                .employeeName("John Doe")
                .email("john.doe@company.com")
                .department("IT")
                .build();

        testExpense = com.finance.admin.entity.Expense.builder()
                .expenseId(1L)
                .employee(testEmployee)
                .description("Business Travel")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .expenseDate(LocalDate.now().minusDays(1))
                .status(ExpenseStatus.PENDING)
                .build();

        testExpenseDto = Expense.builder()
                .expenseId(1L)
                .description("Business Travel")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .status(ExpenseStatus.PENDING)
                .build();
    }

    @Test
    void getPendingExpenses_ShouldReturnPageOfExpenses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<com.finance.admin.entity.Expense> expenses = List.of(testExpense);
        Page<com.finance.admin.entity.Expense> expensePage = new PageImpl<>(expenses, pageable, 1);

        when(expenseRepository.findByStatusAndIsDeletedFalseOrderByCreatedAtAsc(
                eq(ExpenseStatus.PENDING), eq(pageable))).thenReturn(expensePage);
        when(modelMapper.map(any(com.finance.admin.entity.Expense.class), eq(Expense.class))).thenReturn(testExpenseDto);

        // Act
        Page<Expense> result = expenseService.getPendingExpenses(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(expenseRepository).findByStatusAndIsDeletedFalseOrderByCreatedAtAsc(
                eq(ExpenseStatus.PENDING), eq(pageable));
    }

    @Test
    void approveExpense_ShouldApproveExpenseSuccessfully() {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .expenseId(1L)
                .approvedBy("admin")
                .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(currencyService.getExchangeRateToInr("USD")).thenReturn(new BigDecimal("83.00"));
        when(expenseRepository.save(any(com.finance.admin.entity.Expense.class))).thenReturn(testExpense);
        when(modelMapper.map(any(com.finance.admin.entity.Expense.class), eq(Expense.class))).thenReturn(testExpenseDto);

        // Act
        Expense result = expenseService.approveExpense(approvalRequest);

        // Assert
        assertNotNull(result);
        assertEquals(ExpenseStatus.APPROVED, testExpense.getStatus());
        assertEquals("admin", testExpense.getApprovedBy());
        assertEquals(LocalDate.now(), testExpense.getApprovalDate());
        // Email service calls are handled with exception catching
        verify(expenseRepository).save(testExpense);
    }

    @Test
    void approveExpense_ShouldThrowExceptionWhenExpenseNotFound() {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .expenseId(999L)
                .approvedBy("admin")
                .build();

        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> expenseService.approveExpense(approvalRequest));
        verify(expenseRepository, never()).save(any(com.finance.admin.entity.Expense.class));
        // Email service calls are handled with exception catching
    }

    @Test
    void approveExpense_ShouldThrowExceptionWhenExpenseNotPending() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.APPROVED);
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .expenseId(1L)
                .approvedBy("admin")
                .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        // Act & Assert
        assertThrows(BusinessException.class, 
                () -> expenseService.approveExpense(approvalRequest));
        verify(expenseRepository, never()).save(any(com.finance.admin.entity.Expense.class));
        // Email service calls are handled with exception catching
    }

    @Test
    void rejectExpense_ShouldRejectExpenseSuccessfully() {
        // Arrange
        RejectionRequest rejectionRequest = RejectionRequest.builder()
                .expenseId(1L)
                .rejectionReason("Invalid receipt")
                .rejectedBy("admin")
                .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(com.finance.admin.entity.Expense.class))).thenReturn(testExpense);
        when(modelMapper.map(any(com.finance.admin.entity.Expense.class), eq(Expense.class))).thenReturn(testExpenseDto);

        // Act
        Expense result = expenseService.rejectExpense(rejectionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(ExpenseStatus.REJECTED, testExpense.getStatus());
        assertEquals("admin", testExpense.getApprovedBy());
        assertEquals("Invalid receipt", testExpense.getRejectionReason());
        assertEquals(LocalDate.now(), testExpense.getApprovalDate());
        // Email service is called but exceptions are caught and logged
        verify(expenseRepository).save(testExpense);
    }

    @Test
    void getExpenseById_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(modelMapper.map(any(com.finance.admin.entity.Expense.class), eq(Expense.class))).thenReturn(testExpenseDto);

        // Act
        Expense result = expenseService.getExpenseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testExpenseDto.getExpenseId(), result.getExpenseId());
        verify(expenseRepository).findById(1L);
    }

    @Test
    void getExpenseById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> expenseService.getExpenseById(999L));
    }

    @Test
    void getTotalApprovedAmountInr_ShouldReturnTotal() {
        // Arrange
        BigDecimal expectedTotal = new BigDecimal("41500.00");
        when(expenseRepository.getTotalApprovedAmountInr()).thenReturn(expectedTotal);

        // Act
        BigDecimal result = expenseService.getTotalApprovedAmountInr();

        // Assert
        assertEquals(expectedTotal, result);
        verify(expenseRepository).getTotalApprovedAmountInr();
    }

    @Test
    void getTotalApprovedAmountByCurrency_ShouldReturnCurrencyTotals() {
        // Arrange
        Object[] currencyTotal = {"USD", new BigDecimal("500.00"), new BigDecimal("41500.00"), 1L};
        List<Object[]> results = new java.util.ArrayList<>();
        results.add(currencyTotal);
        when(expenseRepository.getTotalApprovedAmountByCurrency()).thenReturn(results);

        // Act
        List<ExpenseReport.CurrencyTotalDto> result = expenseService.getTotalApprovedAmountByCurrency();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).getCurrency());
        assertEquals(new BigDecimal("500.00"), result.get(0).getTotalAmount());
        verify(expenseRepository).getTotalApprovedAmountByCurrency();
    }

    @Test
    void generateExpenseReport_ShouldReturnReport_WhenValidEmployeeIds() {
        // Arrange
        List<Long> employeeIds = List.of(1L);
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 5);
        
        Object[] employeeTotal = {1L, new BigDecimal("41500.00"), 1L};
        List<Object[]> employeeTotals = new java.util.ArrayList<>();
        employeeTotals.add(employeeTotal);
        
        Object[] currencyTotal = {"USD", new BigDecimal("500.00"), new BigDecimal("41500.00"), 1L};
        List<Object[]> currencyTotals = new java.util.ArrayList<>();
        currencyTotals.add(currencyTotal);
        
        when(expenseRepository.getTotalApprovedAmountByEmployee(employeeIds, startDate, endDate))
                .thenReturn(employeeTotals);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(expenseRepository.getCurrencyTotalsByEmployee(1L, startDate, endDate))
                .thenReturn(currencyTotals);
        when(modelMapper.map(any(Employee.class), eq(com.finance.admin.dto.Employee.class)))
                .thenReturn(com.finance.admin.dto.Employee.builder()
                        .employeeId(1L)
                        .employeeName("John Doe")
                        .build());

        // Act
        Page<ExpenseReport> result = expenseService.generateExpenseReport(employeeIds, startDate, endDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(expenseRepository).getTotalApprovedAmountByEmployee(employeeIds, startDate, endDate);
        verify(employeeRepository).findById(1L);
    }

    @Test
    void generateExpenseReport_ShouldThrowException_WhenTooManyEmployees() {
        // Arrange
        List<Long> employeeIds = List.of(1L, 2L, 3L, 4L, 5L, 6L); // More than maxEmployeesForReport
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 5);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> expenseService.generateExpenseReport(employeeIds, startDate, endDate, pageable));
        
        verify(expenseRepository, never()).getTotalApprovedAmountByEmployee(any(), any(), any());
    }

    @Test
    void syncExpenseFromEmployeeService_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(modelMapper.map(any(com.finance.admin.entity.Expense.class), eq(Expense.class))).thenReturn(testExpenseDto);

        // Act
        Expense result = expenseService.syncExpenseFromEmployeeService(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testExpenseDto.getExpenseId(), result.getExpenseId());
        verify(expenseRepository).findById(1L);
    }

    @Test
    void approveExpense_ShouldHandleEmailException_Gracefully() throws EmailException {
        // Arrange
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .expenseId(1L)
                .approvedBy("admin")
                .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(currencyService.getExchangeRateToInr("USD")).thenReturn(new BigDecimal("83.00"));
        when(expenseRepository.save(any(com.finance.admin.entity.Expense.class))).thenReturn(testExpense);
        when(modelMapper.map(any(com.finance.admin.entity.Expense.class), eq(Expense.class))).thenReturn(testExpenseDto);
        doThrow(new EmailException("Email service unavailable")).when(emailService).sendApprovalNotification(any(Expense.class));

        // Act
        Expense result = expenseService.approveExpense(approvalRequest);

        // Assert
        assertNotNull(result);
        assertEquals(ExpenseStatus.APPROVED, testExpense.getStatus());
        verify(expenseRepository).save(testExpense);
        verify(emailService).sendApprovalNotification(any(Expense.class));
    }
}