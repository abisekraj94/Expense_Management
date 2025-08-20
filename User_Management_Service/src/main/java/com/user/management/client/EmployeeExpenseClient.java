package com.user.management.client;

import com.user.management.dto.ApiResponse;
import com.user.management.dto.EmployeeExpenseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "employee-expense-service", url = "${microservice.employee-expense.url}")
public interface EmployeeExpenseClient {
    
    @PostMapping("${microservice.employee.endpoint}")
    ApiResponse<String> registerEmployee(@RequestBody EmployeeExpenseRequest request, 
                                       @RequestHeader("${jwt.header.name}") String token);
}