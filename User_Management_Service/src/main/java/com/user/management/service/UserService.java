package com.user.management.service;

import com.user.management.dto.*;
import com.user.management.exception.*;

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
     * @throws RoleNotFoundException if role not found
     */
    UserProfileResponse registerUser(UserRegistrationRequest registrationRequest) 
            throws EmailAlreadyExistsException, RoleNotFoundException;

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest user login credentials
     * @return authentication response with JWT token
     * @throws InvalidCredentialsException if credentials are invalid
     */
    AuthenticationResponse authenticateUser(UserLoginRequest loginRequest) 
            throws InvalidCredentialsException;

    /**
     * Get user profile by email
     * 
     * @param email user's email address
     * @return user profile response
     * @throws UserNotFoundException if user not found
     */
    UserProfileResponse getUserProfile(String email) throws UserNotFoundException;

    /**
     * Update user profile information
     * 
     * @param email user's email address
     * @param updateRequest profile update details
     * @return updated user profile response
     * @throws UserNotFoundException if user not found
     * @throws RoleNotFoundException if role not found
     */
    UserProfileResponse updateUserProfile(String email, UserRegistrationRequest updateRequest) 
            throws UserNotFoundException, RoleNotFoundException;

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