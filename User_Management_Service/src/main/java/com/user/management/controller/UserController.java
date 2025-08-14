package com.user.management.controller;

import com.user.management.constants.ApplicationConstants;
import com.user.management.dto.ApiResponse;
import com.user.management.dto.UserProfileResponse;
import com.user.management.dto.UserRegistrationRequest;
import com.user.management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for user management operations
 * Handles user profile operations with proper authorization
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("${api.base.path}${api.user.endpoint}")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ApplicationConstants constants;

    /**
     * Get current user's profile
     * 
     * @param principal authenticated user principal
     * @return API response with user profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('FINANCE_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(Principal principal) {
        try {
            log.info("Received get profile request for user: {}", principal.getName());
            
            UserProfileResponse userProfile = userService.getUserProfile(principal.getName());
            ApiResponse<UserProfileResponse> response = ApiResponse.success(
                    constants.PROFILE_RETRIEVED, 
                    userProfile
            );
            
            log.info("Profile retrieved successfully for user: {}", principal.getName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                ApiResponse<UserProfileResponse> response = ApiResponse.error(constants.USER_NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            ApiResponse<UserProfileResponse> response = ApiResponse.error("Failed to retrieve profile");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update current user's profile
     * 
     * @param principal authenticated user principal
     * @param updateRequest profile update details
     * @return API response with updated user profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('FINANCE_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            Principal principal,
            @Valid @RequestBody UserRegistrationRequest updateRequest) {
        try {
            log.info("Received update profile request for user: {}", principal.getName());
            
            UserProfileResponse updatedProfile = userService.updateUserProfile(principal.getName(), updateRequest);
            ApiResponse<UserProfileResponse> response = ApiResponse.success(
                    constants.PROFILE_UPDATED_SUCCESSFULLY, 
                    updatedProfile
            );
            
            log.info("Profile updated successfully for user: {}", principal.getName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                ApiResponse<UserProfileResponse> response = ApiResponse.error(constants.USER_NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            ApiResponse<UserProfileResponse> response = ApiResponse.error("Failed to update profile");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get user profile by email (Finance Admin only)
     * 
     * @param email target user's email address
     * @return API response with user profile
     */
    @GetMapping("/profile/{email}")
    @PreAuthorize("hasRole('FINANCE_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfileByEmail(@PathVariable String email) {
        try {
            log.info("Received get profile request for user: {} by admin", email);
            
            UserProfileResponse userProfile = userService.getUserProfile(email);
            ApiResponse<UserProfileResponse> response = ApiResponse.success(
                    constants.PROFILE_RETRIEVED, 
                    userProfile
            );
            
            log.info("Profile retrieved successfully for user: {} by admin", email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                ApiResponse<UserProfileResponse> response = ApiResponse.error(constants.USER_NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            ApiResponse<UserProfileResponse> response = ApiResponse.error("Failed to retrieve profile");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all employees (Finance Admin only)
     * 
     * @return API response with list of all employees
     */
    @GetMapping("/employees")
    @PreAuthorize("hasRole('FINANCE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllEmployees() {
        try {
            log.info("Received request to get all employees");
            
            java.util.List<UserProfileResponse> employees = userService.getAllEmployees();
            ApiResponse<List<UserProfileResponse>> response = ApiResponse.success(
                    constants.EMPLOYEES_RETRIEVED, 
                    employees
            );
            
            log.info("Retrieved {} employees successfully", employees.size());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            ApiResponse<List<UserProfileResponse>> response = ApiResponse.error("Failed to retrieve employees");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Health check endpoint
     * 
     * @return API response confirming service health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        ApiResponse<String> response = ApiResponse.success(
                constants.SERVICE_RUNNING, 
                constants.SERVICE_STATUS
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}