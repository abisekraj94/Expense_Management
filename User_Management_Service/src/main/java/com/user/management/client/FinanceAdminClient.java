package com.user.management.client;

import com.user.management.dto.ApiResponse;
import com.user.management.dto.FinanceAdminRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "finance-admin-service", url = "${microservice.finance-admin.url}")
public interface FinanceAdminClient {
    
    @PostMapping("${microservice.finance.endpoint}")
    ApiResponse<String> registerAdmin(@RequestBody FinanceAdminRequest request,
                                    @RequestHeader("${jwt.header.name}") String token);
}