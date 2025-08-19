package com.expense.service.service.impl;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.ExpenseRequest;
import com.expense.service.dto.ExpenseResponse;
import com.expense.service.entity.EmployeeExpense;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.exception.GlobalExceptionHandler;
import com.expense.service.exception.GlobalExceptionHandler.ExpenseNotFoundException;
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
import java.util.Optional;

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
    private ExpenseCategory expenseCategory;
    private EmployeeExpense employeeExpense;

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
        
        // Setup mock constants
        constants.BASE_CURRENCY = "INR";
        constants.STATUS_REQUESTED = "Requested";
        constants.CATEGORY_NOT_FOUND = "Expense category not found";
        constants.EXPENSE_NOT_FOUND = "Expense not found";
    }

    @Test
    void createExpense_Success() throws GlobalExceptionHandler.CurrencyConversionException, GlobalExceptionHandler.ExpenseCategoryNotFoundException, GlobalExceptionHandler.BusinessRuleViolationException {
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
        GlobalExceptionHandler.ExpenseCategoryNotFoundException exception = assertThrows(GlobalExceptionHandler.ExpenseCategoryNotFoundException.class, 
            () -> expenseService.createExpense(expenseRequestDto));
        
        assertEquals("Expense category not found", exception.getMessage());
        verify(categoryRepository).findByIdAndIsActiveTrue(1L);
        verifyNoInteractions(currencyService);
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getExpenseById_Success() throws ExpenseNotFoundException {
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
}