package com.finance.admin.controller;

import com.finance.admin.dto.UserLoginRequest;
import com.finance.admin.dto.UserResponse;
import com.finance.admin.exception.AuthenticationException;
import com.finance.admin.exception.UserServiceException;
import com.finance.admin.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserManagementService userManagementService;
    
    @Value("${role.finance.admin}")
    private String financeAdminRole;
    
    @Value("${log.authenticating.user}")
    private String logAuthenticatingUser;
    
    @Value("${log.fetching.user.by.id}")
    private String logFetchingUserById;
    
    @Value("${success.user.authenticated}")
    private String successUserAuthenticated;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) throws AuthenticationException {
        log.info(logAuthenticatingUser + "{}", loginRequest.getUsername());
        
        UserResponse userResponse = userManagementService.authenticateUser(loginRequest);
        
        // Only allow FINANCE_ADMIN role to access this service
        if (!financeAdminRole.equals(userResponse.getRole())) {
            log.warn("Access denied for user {} with role {}", loginRequest.getUsername(), userResponse.getRole());
            return ResponseEntity.status(403).build();
        }
        
        log.info(successUserAuthenticated + "{}", loginRequest.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) throws UserServiceException {
        log.info(logFetchingUserById + "{}", userId);
        
        UserResponse userResponse = userManagementService.getUserById(userId);
        
        return ResponseEntity.ok(userResponse);
    }
}