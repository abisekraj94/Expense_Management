package com.expense.service.service.impl;

import com.expense.service.client.UserManagementClient;
import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.User;
import com.expense.service.service.UserService;
import com.expense.service.exception.GlobalExceptionHandler.UserServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserManagementClient userManagementClient;
    private final ApplicationConstants constants;

    public UserServiceImpl(UserManagementClient userManagementClient, ApplicationConstants constants) {
        this.userManagementClient = userManagementClient;
        this.constants = constants;
    }

    @Override
    public User getUserById(Long userId) throws UserServiceException {
        try {
            return userManagementClient.getUserById(userId);
        } catch (Exception e) {
            log.error("Failed to get user {}: {}", userId, e.getMessage());
            throw new UserServiceException(constants.USER_NOT_FOUND, e);
        }
    }
}