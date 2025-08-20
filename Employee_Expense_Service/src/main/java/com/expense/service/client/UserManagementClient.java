package com.expense.service.client;

import com.expense.service.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-management-service", url = "${user.management.service.url}")
public interface UserManagementClient {
    
    @GetMapping("/api/users/{userId}")
    User getUserById(@PathVariable Long userId);
}