package com.user.management.controller;

import com.user.management.constants.ApplicationConstants;
import com.user.management.dto.response.ApiResponse;
import com.user.management.dto.response.AuthResponse;
import com.user.management.dto.request.LoginRequest;
import com.user.management.dto.request.RegisterRequest;
import com.user.management.dto.response.RegisteredResponse;
import com.user.management.service.AuthService;
import com.user.management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations
 * Handles user registration, login, and logout endpoints
 * 
 * @author User Management Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user
     * 
     * @param registerRequest the registration request data
     * @return ResponseEntity with registration response
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisteredResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        
        log.info("Registration request received for email: {}", registerRequest.getEmail());

        RegisteredResponse response = authService.register(registerRequest);
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ApplicationConstants.USER_REGISTERED_SUCCESSFULLY, response));
    }

    /**
     * Authenticate user login
     * 
     * @param loginRequest the login request data
     * @return ResponseEntity with authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        log.info("Login request received for email: {}", loginRequest.getEmail());
        
        AuthResponse authResponse = authService.login(loginRequest);
        
        return ResponseEntity.ok(ApiResponse.success(ApplicationConstants.USER_LOGGED_IN_SUCCESSFULLY, authResponse));
    }

    /**
     * Logout user and invalidate token
     * 
     * @param request the HTTP request containing JWT token
     * @return ResponseEntity with logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        
        String jwt = getJwtFromRequest(request);
        
        if (StringUtils.hasText(jwt)) {
            try {
                jwtUtil.validateToken(jwt);
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                log.info("Logout request received for user: {}", userId);
                
                authService.logout(userId);
                
                return ResponseEntity.ok(ApiResponse.success(ApplicationConstants.USER_LOGGED_OUT_SUCCESSFULLY));
            } catch (Exception e) {
                log.error("Invalid token during logout: {}", e.getMessage());
            }
        }
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid token", "Token is required for logout"));
    }

    /**
     * Validate JWT token
     * 
     * @param request the HTTP request containing JWT token
     * @return ResponseEntity with validation response
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Object>> validateToken(HttpServletRequest request) {
        
        String jwt = getJwtFromRequest(request);
        
        if (StringUtils.hasText(jwt)) {
            try {
                jwtUtil.validateToken(jwt);
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                
                if (authService.validateTokenInRedis(userId, jwt)) {
                    return ResponseEntity.ok(ApiResponse.success("Token is valid"));
                }
            } catch (Exception e) {
                log.error("Token validation failed: {}", e.getMessage());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid token", "Token validation failed"));
    }

    /**
     * Extract JWT token from request header
     * 
     * @param request the HTTP request
     * @return JWT token if present, null otherwise
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(ApplicationConstants.AUTH_HEADER_NAME);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(ApplicationConstants.AUTH_TOKEN_PREFIX)) {
            return bearerToken.substring(ApplicationConstants.AUTH_TOKEN_PREFIX.length());
        }
        return null;
    }
}