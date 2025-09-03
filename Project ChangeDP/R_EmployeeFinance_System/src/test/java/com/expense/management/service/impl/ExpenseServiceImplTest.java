package com.expense.management.service.impl;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.dto.ExpenseSubmission;
import com.expense.management.dto.ExpenseResponse;
import com.expense.management.entity.Employee;
import com.expense.management.entity.EmployeeExpense;
import com.expense.management.entity.ExpenseCategory;
import com.expense.management.exception.BusinessException;
import com.expense.management.exception.ResourceNotFoundException;
import com.expense.management.repository.EmployeeExpenseRepository;
import com.expense.management.repository.EmployeeRepository;
import com.expense.management.repository.ExpenseCategoryRepository;
import com.expense.management.util.CurrencyExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseServiceImpl
 * Tests business logic and error handling scenarios
 * 
 * @author System
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private EmployeeExpenseRepository expenseRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ExpenseCategoryRepository categoryRepository;

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Employee testEmployee;
    private ExpenseCategory testCategory;
    private ExpenseSubmission testExpenseDto;
    private EmployeeExpense testExpense;

    /**
     * Sets up test data before each test
     */
    @BeforeEach
    void setUp() {
        // Setup test employee
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmailId("john.doe@company.com");

        // Setup test category
        testCategory = new ExpenseCategory();
        testCategory.setId(1);
        testCategory.setCategory("Travel");
        testCategory.setMaxLimit(new BigDecimal("10000.00"));
        testCategory.setIsActive(true);

        // Setup test expense DTO
        testExpenseDto = new ExpenseSubmission();
        testExpenseDto.setEmpId(1L);
        testExpenseDto.setExpenseCategoryId(1);
        testExpenseDto.setDescription("Business travel to client site");
        testExpenseDto.setAmount(new BigDecimal("500.00"));
        testExpenseDto.setCurrency("USD");
        testExpenseDto.setExpenseDate(LocalDate.now().minusDays(1));

        // Setup test expense entity
        testExpense = new EmployeeExpense();
        testExpense.setId(1);
        testExpense.setEmployee(testEmployee);
        testExpense.setExpenseCategory(testCategory);
        testExpense.setDescription(testExpenseDto.getDescription());
        testExpense.setAmount(testExpenseDto.getAmount());
        testExpense.setCurrency(testExpenseDto.getCurrency());
        testExpense.setExpenseDate(testExpenseDto.getExpenseDate());
        testExpense.setStatus(ApplicationConstants.EXPENSE_STATUS_PENDING);
    }

    /**
     * Tests successful expense submission with currency conversion
     */
    @Test
    void testSubmitExpense_Success_WithCurrencyConversion() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(categoryRepository.findByIdAndIsActiveTrue(1)).thenReturn(Optional.of(testCategory));
        when(currencyExchangeService.getExchangeRate("USD", "INR")).thenReturn(new BigDecimal("83.00"));
        when(currencyExchangeService.convertCurrency(any(BigDecimal.class), anyString(), anyString()))
                .thenReturn(new BigDecimal("41500.00"));
        when(expenseRepository.save(any(EmployeeExpense.class))).thenReturn(testExpense);
        when(modelMapper.map(any(EmployeeExpense.class), eq(ExpenseResponse.class)))
                .thenReturn(new ExpenseResponse());

        // Act
        ExpenseResponse result = expenseService.submitExpense(testExpenseDto);

        // Assert
        assertNotNull(result);
        verify(employeeRepository).findById(1L);
        verify(categoryRepository).findByIdAndIsActiveTrue(1);
        verify(currencyExchangeService).getExchangeRate("USD", "INR");
        verify(currencyExchangeService).convertCurrency(testExpenseDto.getAmount(), "USD", "INR");
        verify(expenseRepository).save(any(EmployeeExpense.class));
    }

    /**
     * Tests expense submission failure when employee not found
     */
    @Test
    void testSubmitExpense_EmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.submitExpense(testExpenseDto)
        );

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(expenseRepository, never()).save(any(EmployeeExpense.class));
    }

    /**
     * Tests expense submission failure when amount exceeds category limit
     */
    @Test
    void testSubmitExpense_AmountExceedsLimit() {
        // Arrange
        testExpenseDto.setAmount(new BigDecimal("15000.00")); // Exceeds limit of 10000
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(categoryRepository.findByIdAndIsActiveTrue(1)).thenReturn(Optional.of(testCategory));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> expenseService.submitExpense(testExpenseDto)
        );

        assertTrue(exception.getMessage().contains("exceeds category limit"));
        verify(expenseRepository, never()).save(any(EmployeeExpense.class));
    }

    /**
     * Tests getting expense by ID successfully
     */
    @Test
    void testGetExpenseById_Success() {
        // Arrange
        testExpense.setIsDeleted(false);
        when(expenseRepository.findById(1)).thenReturn(Optional.of(testExpense));
        when(modelMapper.map(testExpense, ExpenseResponse.class)).thenReturn(new ExpenseResponse());

        // Act
        ExpenseResponse result = expenseService.getExpenseById(1);

        // Assert
        assertNotNull(result);
        verify(expenseRepository).findById(1);
        verify(modelMapper).map(testExpense, ExpenseResponse.class);
    }

    /**
     * Tests getting expense by ID when expense not found
     */
    @Test
    void testGetExpenseById_NotFound() {
        // Arrange
        when(expenseRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.getExpenseById(1)
        );

        assertEquals("Expense not found with ID: 1", exception.getMessage());
    }
}