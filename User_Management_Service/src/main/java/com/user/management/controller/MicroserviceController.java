package com.user.management.controller;

import com.user.management.dto.ApiResponse;
import com.user.management.dto.UserProfileResponse;
import com.user.management.exception.MicroserviceCommunicationException;
import com.user.management.exception.UserNotFoundException;
import com.user.management.service.MicroserviceIntegrationService;
import com.user.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/microservice")
@RequiredArgsConstructor
public class MicroserviceController {
    
    private final MicroserviceIntegrationService microserviceIntegrationService;
    private final UserService userService;
    
    @Value("${jwt.token.prefix}")
    private String tokenPrefix;
    
    @Value("${status.success}")
    private String successStatus;
    
    @Value("${microservice.employee.service.notified}")
    private String employeeServiceNotified;
    
    @Value("${microservice.finance.service.notified}")
    private String financeServiceNotified;
    
    @Value("${microservice.notification.sent}")
    private String notificationSent;
    
    @PostMapping("/notify-employee/{email}")
    @PreAuthorize("hasRole('FINANCE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> notifyEmployeeService(@PathVariable String email,
                                                                   @RequestHeader("${jwt.header.name}") String token) 
            throws UserNotFoundException, MicroserviceCommunicationException {
        UserProfileResponse user = userService.getUserProfile(email);
        String jwtToken = token.replace(tokenPrefix, "");
        microserviceIntegrationService.notifyEmployeeExpenseService(user, jwtToken);
        
        return ResponseEntity.ok(new ApiResponse<>(successStatus, employeeServiceNotified, notificationSent));
    }
    
    @PostMapping("/notify-finance/{email}")
    @PreAuthorize("hasRole('FINANCE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> notifyFinanceService(@PathVariable String email,
                                                                  @RequestHeader("${jwt.header.name}") String token) 
            throws UserNotFoundException, MicroserviceCommunicationException {
        UserProfileResponse user = userService.getUserProfile(email);
        String jwtToken = token.replace(tokenPrefix, "");
        microserviceIntegrationService.notifyFinanceAdminService(user, jwtToken);
        
        return ResponseEntity.ok(new ApiResponse<>(successStatus, financeServiceNotified, notificationSent));
    }
}