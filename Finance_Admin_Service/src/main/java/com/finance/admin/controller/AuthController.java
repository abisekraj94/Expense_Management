package com.finance.admin.controller;

import com.finance.admin.dto.LoginRequestDto;
import com.finance.admin.dto.LoginResponseDto;
import com.finance.admin.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations
 * Handles admin login and JWT token management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate admin and generate JWT token
     * 
     * @param loginRequest login credentials
     * @return login response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());
        
        LoginResponseDto response = authService.authenticateAdmin(loginRequest);
        
        log.info("Login successful for username: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }
}