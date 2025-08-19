package com.expense.service.service;

import com.expense.service.dto.CategoryRequest;
import com.expense.service.entity.ExpenseCategory;

public interface CategoryService {
    ExpenseCategory createCategory(CategoryRequest categoryRequest);
}