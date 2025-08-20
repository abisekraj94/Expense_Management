package com.expense.service.service.impl;

import com.expense.service.dto.CategoryRequest;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.exception.GlobalExceptionHandler.CategoryServiceException;
import com.expense.service.repository.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ExpenseCategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryRequest categoryRequest;
    private ExpenseCategory expenseCategory;

    @BeforeEach
    void setUp() {
        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Travel");
        categoryRequest.setSpendingLimit(new BigDecimal("5000.00"));

        expenseCategory = new ExpenseCategory();
        expenseCategory.setId(1L);
        expenseCategory.setCategory("Travel");
        expenseCategory.setMaxLimit(new BigDecimal("5000.00"));
        expenseCategory.setIsActive(true);
    }

    @Test
    void createCategory_Success() throws CategoryServiceException {
        // Arrange
        when(categoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategory);

        // Act
        ExpenseCategory result = categoryService.createCategory(categoryRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Travel", result.getCategory());
        assertEquals(new BigDecimal("5000.00"), result.getMaxLimit());
        assertTrue(result.getIsActive());
        verify(categoryRepository).save(any(ExpenseCategory.class));
    }

    @Test
    void createCategory_DatabaseError_ThrowsException() {
        // Arrange
        when(categoryRepository.save(any(ExpenseCategory.class)))
            .thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        CategoryServiceException exception = assertThrows(CategoryServiceException.class,
            () -> categoryService.createCategory(categoryRequest));
        
        assertEquals("Failed to create category", exception.getMessage());
        verify(categoryRepository).save(any(ExpenseCategory.class));
    }

    @Test
    void createCategory_UnexpectedError_ThrowsException() {
        // Arrange
        when(categoryRepository.save(any(ExpenseCategory.class)))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        CategoryServiceException exception = assertThrows(CategoryServiceException.class,
            () -> categoryService.createCategory(categoryRequest));
        
        assertEquals("Failed to create category", exception.getMessage());
        verify(categoryRepository).save(any(ExpenseCategory.class));
    }
}