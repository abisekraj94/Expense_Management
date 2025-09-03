package com.expense.management.service.impl;

import com.expense.management.dto.UserLoginNotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void handleUserLogin(UserLoginNotificationDto notification) {
        logger.info("User login notification received - User: {}, Role: {}, Time: {}", 
                   notification.getFullname(), notification.getRole(), notification.getLoginTime());
        
        if ("ADMIN".equals(notification.getRole())) {
            handleAdminLogin(notification);
        } else if ("EMPLOYEE".equals(notification.getRole())) {
            handleEmployeeLogin(notification);
        }
    }

    private void handleAdminLogin(UserLoginNotificationDto notification) {
        logger.info("Admin {} logged in at {}", notification.getFullname(), notification.getLoginTime());
    }

    private void handleEmployeeLogin(UserLoginNotificationDto notification) {
        logger.info("Employee {} logged in at {}", notification.getFullname(), notification.getLoginTime());
    }
}