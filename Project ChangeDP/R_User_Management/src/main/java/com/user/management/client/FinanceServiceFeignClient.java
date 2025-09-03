package com.user.management.client;

import com.user.management.dto.request.LoginNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "finance-service", url = "${finance.service.base-url}")
public interface FinanceServiceFeignClient {
    
    @PostMapping("/notifications/login")
    void sendLoginNotification(@RequestBody LoginNotificationRequest request);
}