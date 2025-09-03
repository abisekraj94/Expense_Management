package com.user.management.controller;

import com.user.management.dto.response.ApiResponse;
import com.user.management.dto.User;
import com.user.management.service.UserService;
import com.user.management.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * REST controller for admin-specific operations
 * Handles administrative functions with admin-only access
 * 
 * @author User Management Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    /**
     * Get all users with detailed information
     * 
     * @return ResponseEntity with list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersDetailed() {
        
        log.info("Admin request: Get all users with details");
        
        List<User> users = userService.getAllUsers();
        
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", users));
    }
}