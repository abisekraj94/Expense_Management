package com.user.management.service;

import com.user.management.dto.User;
import com.user.management.entity.Employee;

import java.util.List;

/**
 * Service interface for user management operations
 * Defines contract for user CRUD operations
 * 
 * @author User Management Team
 * @version 1.0
 */
public interface UserService {

    /**
     * Get user by ID
     * 
     * @param userId the user ID
     * @return user DTO
     */
    User getUserById(Long userId);

    /**
     * Get user by email
     * 
     * @param email the user email
     * @return user DTO
     */
    User getUserByEmail(String email);

    /**
     * Get all users (Admin only)
     * 
     * @return list of user DTOs
     */
    List<User> getAllUsers();

    /**
     * Update user information
     * 
     * @param userId the user ID
     * @param userDto the updated user data
     * @return updated user DTO
     */
    User updateUser(Long userId, User userDto);

    /**
     * Delete user (Admin only)
     * 
     * @param userId the user ID to delete
     */
    void deleteUser(Long userId);

    /**
     * Convert Employee entity to UserDto
     * 
     * @param employee the employee entity
     * @return user DTO
     */
    User convertToDto(Employee employee);
}