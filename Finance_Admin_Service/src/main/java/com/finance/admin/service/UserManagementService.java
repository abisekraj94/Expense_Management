package com.finance.admin.service;

import com.finance.admin.dto.UserLoginRequest;
import com.finance.admin.dto.UserResponse;
import com.finance.admin.exception.AuthenticationException;
import com.finance.admin.exception.UserServiceException;

public interface UserManagementService {
    
    UserResponse authenticateUser(UserLoginRequest loginRequest) throws AuthenticationException;
    
    UserResponse getUserById(Long userId) throws UserServiceException;
}