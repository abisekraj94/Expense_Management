package com.user.management.service;

import com.user.management.dto.AuthenticationResponse;
import com.user.management.dto.UserLoginRequest;
import com.user.management.dto.UserProfileResponse;
import com.user.management.dto.UserRegistrationRequest;
import com.user.management.dto.*;

/**
 * Service interface for user management operations
 * Defines contract for user registration, authentication, and profile management
 * 
 * @author User Management Team
 * @version 1.0.0
 */
public interface UserService {

    /**
     * Register a new user in the system
     * 
     * @param registrationRequest user registration details
     * @return user profile response
     * @throws EmailAlreadyExistsException if email already exists
     */
    UserProfileResponse registerUser(UserRegistrationRequest registrationRequest);

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest user login credentials
     * @return authentication response with JWT token
     * @throws InvalidCredentialsException if credentials are invalid
     */
    AuthenticationResponse authenticateUser(UserLoginRequest loginRequest);

    /**
     * Get user profile by email
     * 
     * @param email user's email address
     * @return user profile response
     * @throws UserNotFoundException if user not found
     */
    UserProfileResponse getUserProfile(String email);

    /**
     * Update user profile information
     * 
     * @param email user's email address
     * @param updateRequest profile update details
     * @return updated user profile response
     * @throws UserNotFoundException if user not found
     */
    UserProfileResponse updateUserProfile(String email, UserRegistrationRequest updateRequest);

    /**
     * Logout user by removing JWT token from Redis
     * 
     * @param email user's email address
     */
    void logoutUser(String email);

    /**
     * Get all employees (Finance Admin only)
     * 
     * @return list of all employee profiles
     */
    java.util.List<UserProfileResponse> getAllEmployees();
}