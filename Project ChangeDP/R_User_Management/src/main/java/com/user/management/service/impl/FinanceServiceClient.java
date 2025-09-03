package com.user.management.service.impl;

import com.user.management.client.FinanceServiceFeignClient;
import com.user.management.dto.request.LoginNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceServiceClient {

    private final FinanceServiceFeignClient financeServiceFeignClient;

    public void sendLoginNotification(Long userId, String email, String fullName, String role) {
        try {
            LoginNotificationRequest request = new LoginNotificationRequest(
                userId,
                email,
                fullName,
                role,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            
            financeServiceFeignClient.sendLoginNotification(request);
            log.info("Login notification sent to Finance service for user: {}", email);
        } catch (Exception e) {
            log.error("Failed to send login notification to Finance service for user: {}", email, e);
        }
    }
}