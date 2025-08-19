package com.expense.service.service.impl;

import com.expense.service.dto.CategoryRequest;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.repository.ExpenseCategoryRepository;
import com.expense.service.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    public CategoryServiceImpl(ExpenseCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ExpenseCategory createCategory(CategoryRequest categoryRequest) {
        log.info("Creating expense category: {}", categoryRequest.getName());
        
        ExpenseCategory category = new ExpenseCategory();
        category.setCategory(categoryRequest.getName());
        category.setMaxLimit(categoryRequest.getSpendingLimit());
        category.setIsActive(true);
        
        ExpenseCategory savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        
        return savedCategory;
    }
}