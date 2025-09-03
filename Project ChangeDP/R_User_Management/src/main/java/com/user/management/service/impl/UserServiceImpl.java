package com.user.management.service.impl;

import com.user.management.dto.User;
import com.user.management.entity.Employee;
import com.user.management.constants.AppErrorCodes;
import com.user.management.exception.UserOperationException;
import com.user.management.repository.EmployeeRepository;
import com.user.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface
 * Handles user management operations
 * 
 * @author User Management Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final FinanceServiceClient financeServiceClient;

    /**
     * Get user by ID
     * 
     * @param userId the user ID
     * @return user DTO
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        
        try {
            Employee employee = employeeRepository.findByEmailIdWithRoles(
                    employeeRepository.findById(userId)
                            .orElseThrow(() -> new UserOperationException(AppErrorCodes.USER_NOT_FOUND))
                            .getEmailId()
            ).orElseThrow(() -> new UserOperationException(AppErrorCodes.USER_NOT_FOUND));
            
            return convertToDto(employee);
        } catch (Exception e) {
            log.error("Error fetching user by ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    /**
     * Get user by email
     * 
     * @param email the user email
     * @return user DTO
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        
        try {
            Employee employee = employeeRepository.findByEmailIdWithRoles(email)
                    .orElseThrow(() -> new UserOperationException(AppErrorCodes.USER_NOT_FOUND));
            
            return convertToDto(employee);
        } catch (Exception e) {
            log.error("Error fetching user by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    /**
     * Get all users (Admin only)
     * 
     * @return list of user DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        
        try {
            List<Employee> employees = employeeRepository.findAll();
            return employees.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    /**
     * Update user information
     * 
     * @param userId the user ID
     * @param userDto the updated user data
     * @return updated user DTO
     */
    @Override
    public User updateUser(Long userId, User userDto) {
        log.info("Updating user: {}", userId);
        
        try {
            Employee employee = employeeRepository.findById(userId)
                    .orElseThrow(() -> new UserOperationException(AppErrorCodes.USER_NOT_FOUND));
            
            // Update allowed fields
            if (userDto.getFirstName() != null) {
                employee.setFirstName(userDto.getFirstName());
            }
            if (userDto.getLastName() != null) {
                employee.setLastName(userDto.getLastName());
            }
            if (userDto.getMobileNo() != null) {
                employee.setMobileNo(userDto.getMobileNo());
            }
            
            Employee updatedEmployee = employeeRepository.save(employee);
            log.info("User updated successfully: {}", userId);
            
            return convertToDto(updatedEmployee);
        } catch (Exception e) {
            log.error("Error updating user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    /**
     * Delete user (Admin only)
     * 
     * @param userId the user ID to delete
     */
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);
        
        try {
            Employee employee = employeeRepository.findById(userId)
                    .orElseThrow(() -> new UserOperationException(AppErrorCodes.USER_NOT_FOUND));
            
            employeeRepository.delete(employee);
            log.info("User deleted successfully: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /**
     * Convert Employee entity to UserDto
     * 
     * @param employee the employee entity
     * @return user DTO
     */
    @Override
    public User convertToDto(Employee employee) {
        User userDto = modelMapper.map(employee, User.class);
        userDto.setEmail(employee.getEmailId());
        
        // Set roles if available
        if (employee.getEmployeeRoleMappings() != null) {
            List<String> roles = employee.getEmployeeRoleMappings().stream()
                    .map(mapping -> mapping.getRole().getRoleName())
                    .collect(Collectors.toList());
            userDto.setRoles(roles);
        }
        
        return userDto;
    }
}