package com.expense.service.service;

import com.expense.service.dto.CategoryRequest;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.exception.GlobalExceptionHandler.CategoryServiceException;

public interface CategoryService {
    ExpenseCategory createCategory(CategoryRequest categoryRequest) throws CategoryServiceException;
}