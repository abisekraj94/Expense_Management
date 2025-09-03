package com.expense.management.controller;

import com.expense.management.dto.UserLoginNotificationDto;
import com.expense.management.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/login")
    public ResponseEntity<String> receiveUserLoginNotification(@RequestBody UserLoginNotificationDto notification) {
        notificationService.handleUserLogin(notification);
        return ResponseEntity.ok("Login notification received");
    }
}