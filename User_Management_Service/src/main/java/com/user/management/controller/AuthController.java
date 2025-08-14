package com.user.management.controller;

import com.user.management.constants.ApplicationConstants;
import com.user.management.dto.*;
import com.user.management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations
 * Handles user registration and login endpoints
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("${api.base.path}${api.auth.endpoint}")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final ApplicationConstants constants;

    /**
     * Register a new user
     * 
     * @param registrationRequest user registration details
     * @return API response with user profile
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest registrationRequest) {
        try {
            log.info("Received user registration request for email: {}", registrationRequest.getEmail());
            
            UserProfileResponse userProfile = userService.registerUser(registrationRequest);
            ApiResponse<UserProfileResponse> response = ApiResponse.success(
                    constants.USER_REGISTERED_SUCCESSFULLY, 
                    userProfile
            );
            
            log.info("User registration completed successfully for email: {}", registrationRequest.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                ApiResponse<UserProfileResponse> response = ApiResponse.error(constants.EMAIL_ALREADY_EXISTS);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            ApiResponse<UserProfileResponse> response = ApiResponse.error("Registration failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest user login credentials
     * @return API response with authentication details
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> loginUser(
            @Valid @RequestBody UserLoginRequest loginRequest) {
        try {
            log.info("Received login request for email: {}", loginRequest.getEmail());
            
            AuthenticationResponse authResponse = userService.authenticateUser(loginRequest);
            ApiResponse<AuthenticationResponse> response = ApiResponse.success(
                    constants.LOGIN_SUCCESSFUL, 
                    authResponse
            );
            
            log.info("User login completed successfully for email: {}", loginRequest.getEmail());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("credentials")) {
                ApiResponse<AuthenticationResponse> response = ApiResponse.error(constants.INVALID_CREDENTIALS);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            ApiResponse<AuthenticationResponse> response = ApiResponse.error("Authentication failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout user
     * 
     * @param email user's email address from JWT token
     * @return API response confirming logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logoutUser(@RequestParam String email) {
        try {
            log.info("Received logout request for email: {}", email);
            
            userService.logoutUser(email);
            ApiResponse<Object> response = ApiResponse.success(constants.LOGOUT_SUCCESS, null);
            
            log.info("User logout completed successfully for email: {}", email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            ApiResponse<Object> response = ApiResponse.error("Logout failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}