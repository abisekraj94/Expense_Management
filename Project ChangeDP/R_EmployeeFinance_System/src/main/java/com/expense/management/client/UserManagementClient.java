package com.expense.management.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-management", url = "${user.management.base-url:http://localhost:8081}", fallback = UserManagementClientFallback.class)
public interface UserManagementClient {
    
    @GetMapping("${user.management.login.endpoint:/api/auth/login}")
    Object getUserDetails();
}