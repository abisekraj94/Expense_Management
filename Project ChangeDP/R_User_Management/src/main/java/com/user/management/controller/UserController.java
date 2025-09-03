package com.user.management.controller;

import com.user.management.dto.response.ApiResponse;
import com.user.management.dto.User;
import com.user.management.service.UserService;
import com.user.management.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * REST controller for user management operations
 * Handles user CRUD operations with role-based access control
 * 
 * @author User Management Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     * 
     * @param authentication the authentication object
     * @return ResponseEntity with user profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ApiResponse<User>> getCurrentUserProfile(Authentication authentication) {
        
        String email = authentication.getName();
        log.info("Profile request for user: {}", email);
        
        User userDto = userService.getUserByEmail(email);
        
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", userDto));
    }

    /**
     * Get user by ID
     * 
     * @param userId the user ID
     * @return ResponseEntity with user data
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) {
        
        log.info("Get user request for ID: {}", userId);
        
        User userDto = userService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success("User retrieved", userDto));
    }

    /**
     * Get all users (Admin only)
     * 
     * @return ResponseEntity with list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        
        log.info("Get all users request");
        
        List<User> users = userService.getAllUsers();
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    /**
     * Update user profile
     * 
     * @param userId the user ID
     * @param userDto the updated user data
     * @param authentication the authentication object
     * @return ResponseEntity with updated user data
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and @userService.getUserByEmail(authentication.name).id == #userId)")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long userId,
            @RequestBody User userDto,
            Authentication authentication,
            HttpServletRequest request) {
        
        log.info("Update user request for ID: {} by user: {}", userId, authentication.getName());
        
        String token = request.getHeader("Authorization");
        User updatedUser = userService.updateUser(userId, userDto);
        
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    /**
     * Delete user (Admin only)
     * 
     * @param userId the user ID to delete
     * @return ResponseEntity with deletion confirmation
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteUser(
            @PathVariable Long userId,
            HttpServletRequest request) {
        
        log.info("Delete user request for ID: {}", userId);
        
        String token = request.getHeader("Authorization");
        userService.deleteUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}