package com.expense.service.service.impl;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.*;
import com.expense.service.entity.EmployeeExpense;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.exception.GlobalExceptionHandler.*;
import com.expense.service.repository.EmployeeExpenseDocRepository;
import com.expense.service.repository.EmployeeExpenseRepository;
import com.expense.service.repository.ExpenseCategoryRepository;
import com.expense.service.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseServiceImpl
 * Tests business logic with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private EmployeeExpenseRepository expenseRepository;

    @Mock
    private ExpenseCategoryRepository categoryRepository;

    @Mock
    private EmployeeExpenseDocRepository docRepository;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationConstants constants;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private ExpenseRequest expenseRequestDto;
    private ExpenseUpdateRequest expenseUpdateRequest;
    private ExpenseUpdate expenseUpdate;
    private ExpenseCategory expenseCategory;
    private EmployeeExpense employeeExpense;
    private DeleteRequest deleteRequest;

    @BeforeEach
    void setUp() {
        expenseRequestDto = new ExpenseRequest();
        expenseRequestDto.setEmployeeId(1L);
        expenseRequestDto.setExpenseCategoryId(1L);
        expenseRequestDto.setCurrency("USD");
        expenseRequestDto.setAmount(new BigDecimal("100.00"));
        expenseRequestDto.setDescription("Test expense");
        expenseRequestDto.setDateOfExpense(LocalDate.now());

        expenseCategory = new ExpenseCategory();
        expenseCategory.setId(1L);
        expenseCategory.setCategory("travel");
        expenseCategory.setMaxLimit(new BigDecimal("1000.00"));

        employeeExpense = new EmployeeExpense();
        employeeExpense.setId(1L);
        employeeExpense.setEmployeeId(1L);
        employeeExpense.setExpenseCategory(expenseCategory);
        employeeExpense.setCurrency("USD");
        employeeExpense.setAmount(new BigDecimal("100.00"));
        employeeExpense.setAmountInr(new BigDecimal("800.00"));
        employeeExpense.setDescription("Test expense");
        employeeExpense.setStatus("Requested");
        employeeExpense.setIsActive(true);
        employeeExpense.setCreatedAt(LocalDateTime.now());
        employeeExpense.setUpdatedAt(LocalDateTime.now());
        
        expenseUpdateRequest = new ExpenseUpdateRequest();
        expenseUpdateRequest.setExpenseCategoryId(1L);
        expenseUpdateRequest.setCurrency("EUR");
        expenseUpdateRequest.setAmount(new BigDecimal("200.00"));
        expenseUpdateRequest.setDescription("Updated expense");
        
        expenseUpdate = new ExpenseUpdate();
        expenseUpdate.setStatus("Approved");
        expenseUpdate.setReviewedBy(2L);
        
        deleteRequest = new DeleteRequest();
        deleteRequest.setEmployeeId(1L);
        
        // Setup mock constants
        constants.BASE_CURRENCY = "INR";
        constants.STATUS_REQUESTED = "Requested";
        constants.CATEGORY_NOT_FOUND = "Expense category not found";
        constants.EXPENSE_NOT_FOUND = "Expense not found";
        constants.UNAUTHORIZED_ACCESS = "Unauthorized access";
        constants.CAN_ONLY_UPDATE_REQUESTED = "Can only update expenses in Requested status";
        constants.CAN_ONLY_DELETE_REQUESTED = "Can only delete own expenses in Requested status";
    }

    @Test
    void createExpense_Success() throws CurrencyConversionException, ExpenseCategoryNotFoundException, BusinessRuleViolationException, DatabaseOperationException {
        // Arrange
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(expenseCategory));
        when(currencyService.convertCurrency(any(), eq("USD"), eq("INR"))).thenReturn(new BigDecimal("800.00"));
        when(expenseRepository.save(any(EmployeeExpense.class))).thenReturn(employeeExpense);

        // Act
        ExpenseResponse result = expenseService.createExpense(expenseRequestDto);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findByIdAndIsActiveTrue(1L);
        verify(currencyService).convertCurrency(any(), eq("USD"), eq("INR"));
        verify(expenseRepository).save(any(EmployeeExpense.class));
    }

    @Test
    void createExpense_CategoryNotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ExpenseCategoryNotFoundException exception = assertThrows(ExpenseCategoryNotFoundException.class, 
            () -> expenseService.createExpense(expenseRequestDto));
        
        assertEquals("Expense category not found", exception.getMessage());
        verify(categoryRepository).findByIdAndIsActiveTrue(1L);
        verifyNoInteractions(currencyService);
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getExpenseById_Success() throws ExpenseNotFoundException, DatabaseOperationException {
        // Arrange
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));

        // Act
        ExpenseResponse result = expenseService.getExpenseById(1L);

        // Assert
        assertNotNull(result);
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void getExpenseById_NotFound_ThrowsException() {
        // Arrange
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class, 
            () -> expenseService.getExpenseById(1L));
        
        assertEquals("Expense not found", exception.getMessage());
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void getExpensesByEmployeeId_Success() throws DatabaseOperationException {
        // Arrange
        List<EmployeeExpense> expenses = Arrays.asList(employeeExpense);
        when(expenseRepository.findByEmployeeIdAndIsActiveTrue(1L)).thenReturn(expenses);

        // Act
        List<ExpenseResponse> result = expenseService.getExpensesByEmployeeId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(expenseRepository).findByEmployeeIdAndIsActiveTrue(1L);
    }

    @Test
    void updateExpense_Success() throws Exception {
        // Arrange
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(expenseCategory));
        when(currencyService.convertCurrency(any(), eq("EUR"), eq("INR"))).thenReturn(new BigDecimal("1600.00"));
        when(expenseRepository.save(any(EmployeeExpense.class))).thenReturn(employeeExpense);

        // Act
        ExpenseResponse result = expenseService.updateExpense(1L, expenseUpdateRequest, 1L);

        // Assert
        assertNotNull(result);
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
        verify(expenseRepository).save(any(EmployeeExpense.class));
    }

    @Test
    void updateExpense_UnauthorizedAccess_ThrowsException() {
        // Arrange
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
            () -> expenseService.updateExpense(1L, expenseUpdateRequest, 2L));
        
        assertEquals("Unauthorized access", exception.getMessage());
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void updateExpense_InvalidStatus_ThrowsException() {
        // Arrange
        employeeExpense.setStatus("Approved");
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));

        // Act & Assert
        InvalidExpenseStatusException exception = assertThrows(InvalidExpenseStatusException.class,
            () -> expenseService.updateExpense(1L, expenseUpdateRequest, 1L));
        
        assertEquals("Can only update expenses in Requested status", exception.getMessage());
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void updateExpenseStatus_Success() throws ExpenseNotFoundException {
        // Arrange
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));
        when(expenseRepository.save(any(EmployeeExpense.class))).thenReturn(employeeExpense);

        // Act
        ExpenseResponse result = expenseService.updateExpenseStatus(1L, expenseUpdate);

        // Assert
        assertNotNull(result);
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
        verify(modelMapper).map(expenseUpdate, employeeExpense);
        verify(expenseRepository).save(employeeExpense);
    }

    @Test
    void deleteExpense_Success() throws Exception {
        // Arrange
        when(expenseRepository.existsByIdAndEmployeeIdAndStatus(1L, 1L, "Requested")).thenReturn(true);
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));
        when(expenseRepository.save(any(EmployeeExpense.class))).thenReturn(employeeExpense);

        // Act
        expenseService.deleteExpense(1L, 1L);

        // Assert
        verify(expenseRepository).existsByIdAndEmployeeIdAndStatus(1L, 1L, "Requested");
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
        verify(expenseRepository).save(employeeExpense);
    }

    @Test
    void deleteExpense_UnauthorizedAccess_ThrowsException() {
        // Arrange
        when(expenseRepository.existsByIdAndEmployeeIdAndStatus(1L, 1L, "Requested")).thenReturn(false);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
            () -> expenseService.deleteExpense(1L, 1L));
        
        assertEquals("Can only delete own expenses in Requested status", exception.getMessage());
        verify(expenseRepository).existsByIdAndEmployeeIdAndStatus(1L, 1L, "Requested");
    }

    @Test
    void deleteExpenseByAdmin_Success() throws ExpenseNotFoundException {
        // Arrange
        when(expenseRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(employeeExpense));
        when(expenseRepository.save(any(EmployeeExpense.class))).thenReturn(employeeExpense);

        // Act
        expenseService.deleteExpenseByAdmin(1L);

        // Assert
        verify(expenseRepository).findByIdAndIsActiveTrue(1L);
        verify(expenseRepository).save(employeeExpense);
    }

    @Test
    void createExpense_ExceedsSpendingLimit_ThrowsException() throws Exception {
        // Arrange
        expenseCategory.setMaxLimit(new BigDecimal("50.00"));
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(expenseCategory));
        when(currencyService.convertCurrency(any(), eq("USD"), eq("INR"))).thenReturn(new BigDecimal("800.00"));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
            () -> expenseService.createExpense(expenseRequestDto));
        
        assertTrue(exception.getMessage().contains("exceeds category spending limit"));
        verify(categoryRepository).findByIdAndIsActiveTrue(1L);
        verify(currencyService).convertCurrency(any(), eq("USD"), eq("INR"));
    }

    @Test
    void createExpense_InvalidEmployeeId_ThrowsException() throws Exception {
        // Arrange
        expenseRequestDto.setEmployeeId(null);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
            () -> expenseService.createExpense(expenseRequestDto));
        
        assertTrue(exception.getMessage().contains("Invalid employee ID"));
    }

    @Test
    void createExpense_InvalidAmount_ThrowsException() throws Exception {
        // Arrange
        expenseRequestDto.setAmount(BigDecimal.ZERO);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
            () -> expenseService.createExpense(expenseRequestDto));
        
        assertTrue(exception.getMessage().contains("Amount must be greater than zero"));
    }

    @Test
    void getExpenseById_InvalidId_ThrowsException() throws Exception {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.getExpenseById(null));
        
        assertEquals(constants.INVALID_EMPLOYEE_ID, exception.getMessage());
    }
}