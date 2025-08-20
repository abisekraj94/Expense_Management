package com.expense.service.service;

import com.expense.service.dto.User;
import com.expense.service.exception.GlobalExceptionHandler.UserServiceException;

public interface UserService {
    User getUserById(Long userId) throws UserServiceException;
}