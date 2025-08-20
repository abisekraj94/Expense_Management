package com.expense.service.service.impl;

import com.expense.service.dto.CategoryRequest;
import com.expense.service.entity.ExpenseCategory;
import com.expense.service.repository.ExpenseCategoryRepository;
import com.expense.service.service.CategoryService;
import com.expense.service.exception.GlobalExceptionHandler.CategoryServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
    public ExpenseCategory createCategory(CategoryRequest categoryRequest) throws CategoryServiceException {
        log.info("Creating expense category: {}", categoryRequest.getName());
        
        try {
            ExpenseCategory category = new ExpenseCategory();
            category.setCategory(categoryRequest.getName());
            category.setMaxLimit(categoryRequest.getSpendingLimit());
            category.setIsActive(true);
            
            ExpenseCategory savedCategory = categoryRepository.save(category);
            log.info("Category created successfully with ID: {}", savedCategory.getId());
            
            return savedCategory;
        } catch (DataAccessException e) {
            log.error("Database error while creating category: {}", e.getMessage());
            throw new CategoryServiceException("Failed to create category", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating category: {}", e.getMessage(), e);
            throw new CategoryServiceException("Failed to create category", e);
        }
    }
}