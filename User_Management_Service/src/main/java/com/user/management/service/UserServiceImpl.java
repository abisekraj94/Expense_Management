package com.user.management.service;

import com.user.management.constants.ApplicationConstants;
import com.user.management.dto.AuthenticationResponse;
import com.user.management.dto.UserLoginRequest;
import com.user.management.dto.UserProfileResponse;
import com.user.management.dto.UserRegistrationRequest;
import com.user.management.dto.*;
import com.user.management.entity.UserMgnt;
import com.user.management.entity.UserRole;

import com.user.management.repository.UserMgntRepository;
import com.user.management.repository.UserRoleRepository;
import com.user.management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for user management operations
 * Handles user registration, authentication, and profile management
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMgntRepository userMgntRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final ApplicationConstants constants;

    /**
     * Register a new user in the system
     * 
     * @param registrationRequest user registration details
     * @return user profile response
    // * @throws EmailAlreadyExistsException if email already exists
     */
    @Override
    public UserProfileResponse registerUser(UserRegistrationRequest registrationRequest) {
        try {
            log.info("Attempting to register user with email: {}", registrationRequest.getEmail());

            // Check if email already exists
            if (userMgntRepository.existsByEmail(registrationRequest.getEmail())) {
                log.warn("Registration failed - email already exists: {}", registrationRequest.getEmail());
                throw new RuntimeException("Email already exists");
            }

            // Find user role
            UserRole userRole = userRoleRepository.findByRoleName(registrationRequest.getRole())
                    .orElseThrow(() -> {
                        log.error("Role not found: {}", registrationRequest.getRole());
                        return new RuntimeException("Role not found: " + registrationRequest.getRole());
                    });

            // Create new user entity
            UserMgnt newUser = new UserMgnt(
                    registrationRequest.getName(),
                    registrationRequest.getEmail(),
                    passwordEncoder.encode(registrationRequest.getPassword()),
                    registrationRequest.getDepartment(),
                    userRole
            );

            // Save user to database
            UserMgnt savedUser = userMgntRepository.save(newUser);
            log.info("User registered successfully with ID: {}", savedUser.getUserId());

            // Map to response DTO
            UserProfileResponse response = modelMapper.map(savedUser, UserProfileResponse.class);
            response.setRole(savedUser.getUserRole().getRoleName());

            return response;
        } catch (RuntimeException e) {
            log.error("Error during user registration for email: {}", registrationRequest.getEmail(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error during user registration for email: {}", registrationRequest.getEmail(), e);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest user login credentials
     * @return authentication response with JWT token
    // * @throws InvalidCredentialsException if credentials are invalid
     */
    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticateUser(UserLoginRequest loginRequest) {
        try {
            log.info("Attempting to authenticate user: {}", loginRequest.getEmail());

            // Find active user by email
            UserMgnt user = userMgntRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Authentication failed - user not found or inactive: {}", loginRequest.getEmail());
                        return new RuntimeException("Invalid credentials");
                    });

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Authentication failed - invalid password for user: {}", loginRequest.getEmail());
                throw new RuntimeException("Invalid credentials");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getUserRole().getRoleName());

            // Map user to profile response
            UserProfileResponse userProfile = modelMapper.map(user, UserProfileResponse.class);
            userProfile.setRole(user.getUserRole().getRoleName());

            log.info("User authenticated successfully: {}", loginRequest.getEmail());

            return new AuthenticationResponse(token, jwtUtil.getTokenExpiration(), userProfile);
        } catch (RuntimeException e) {
            log.error("Error during user authentication for email: {}", loginRequest.getEmail(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error during user authentication for email: {}", loginRequest.getEmail(), e);
            throw new RuntimeException("Authentication failed", e);
        }
    }

    /**
     * Get user profile by email
     * 
     * @param email user's email address
     * @return user profile response
    // * @throws UserNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String email) {
        try {
            log.info("Fetching user profile for email: {}", email);

            UserMgnt user = userMgntRepository.findByEmailAndIsActiveTrue(email)
                    .orElseThrow(() -> {
                        log.warn("User profile not found for email: {}", email);
                        return new RuntimeException("User not found");
                    });

            UserProfileResponse response = modelMapper.map(user, UserProfileResponse.class);
            response.setRole(user.getUserRole().getRoleName());

            log.info("User profile fetched successfully for email: {}", email);
            return response;
        } catch (RuntimeException e) {
            log.error("Error fetching user profile for email: {}", email, e);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user profile for email: {}", email, e);
            throw new RuntimeException("Failed to fetch user profile", e);
        }
    }

    /**
     * Update user profile information
     * 
     * @param email user's email address
     * @param updateRequest profile update details
     * @return updated user profile response
     //* @throws UserNotFoundException if user not found
     */
    @Override
    public UserProfileResponse updateUserProfile(String email, UserRegistrationRequest updateRequest) {
        try {
            log.info("Updating user profile for email: {}", email);

            UserMgnt user = userMgntRepository.findByEmailAndIsActiveTrue(email)
                    .orElseThrow(() -> {
                        log.warn("User not found for profile update: {}", email);
                        return new RuntimeException("User not found");
                    });

            // Update user information
            user.setName(updateRequest.getName());
            user.setDepartment(updateRequest.getDepartment());
            user.setUpdatedBy(email);

            // Update role if changed
            if (!user.getUserRole().getRoleName().equals(updateRequest.getRole())) {
                UserRole newRole = userRoleRepository.findByRoleName(updateRequest.getRole())
                        .orElseThrow(() -> {
                            log.error("Role not found: {}", updateRequest.getRole());
                            return new RuntimeException("Role not found: " + updateRequest.getRole());
                        });
                user.setUserRole(newRole);
            }

            // Update password if provided
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }

            UserMgnt updatedUser = userMgntRepository.save(user);

            UserProfileResponse response = modelMapper.map(updatedUser, UserProfileResponse.class);
            response.setRole(updatedUser.getUserRole().getRoleName());

            log.info("User profile updated successfully for email: {}", email);
            return response;
        } catch (RuntimeException e) {
            log.error("Error updating user profile for email: {}", email, e);
            throw e;
        } catch (Exception e) {
            log.error("Error updating user profile for email: {}", email, e);
            throw new RuntimeException("Failed to update user profile", e);
        }
    }

    /**
     * Logout user by removing JWT token from Redis
     * 
     * @param email user's email address
     */
    @Override
    public void logoutUser(String email) {
        try {
            log.info("Logging out user: {}", email);
            jwtUtil.removeTokenFromRedis(email);
            log.info("User logged out successfully: {}", email);
        } catch (RuntimeException e) {
            log.error("Error during user logout for email: {}", email, e);
            throw e;
        } catch (Exception e) {
            log.error("Error during user logout for email: {}", email, e);
            throw new RuntimeException("Logout failed", e);
        }
    }

    /**
     * Get all employees (Finance Admin only)
     * 
     * @return list of all employee profiles
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<UserProfileResponse> getAllEmployees() {
        try {
            log.info("Fetching all employees");
            
            java.util.List<UserMgnt> employees = userMgntRepository.findByIsActiveTrueOrderByCreatedDateDesc();
            
            java.util.List<UserProfileResponse> employeeProfiles = employees.stream()
                    .map(user -> {
                        UserProfileResponse profile = modelMapper.map(user, UserProfileResponse.class);
                        profile.setRole(user.getUserRole().getRoleName());
                        return profile;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            log.info("Retrieved {} employees successfully", employeeProfiles.size());
            return employeeProfiles;
        } catch (RuntimeException e) {
            log.error("Error fetching all employees", e);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching all employees", e);
            throw new RuntimeException("Failed to fetch employees", e);
        }
    }
}