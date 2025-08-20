package com.user.management.util;

import com.user.management.dto.*;
import com.user.management.entity.UserMgnt;
import com.user.management.entity.UserRole;

import java.time.LocalDateTime;

public class TestDataBuilder {

    public static UserRegistrationRequest createUserRegistrationRequest() {
        return new UserRegistrationRequest(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "IT",
                "EMPLOYEE"
        );
    }

    public static UserLoginRequest createUserLoginRequest() {
        return new UserLoginRequest(
                "john.doe@example.com",
                "password123"
        );
    }

    public static UserRole createUserRole() {
        UserRole role = new UserRole("EMPLOYEE", "Employee Role");
        role.setRoleId(1L);
        role.setCreatedDate(LocalDateTime.now());
        role.setUpdatedDate(LocalDateTime.now());
        return role;
    }

    public static UserRole createFinanceAdminRole() {
        UserRole role = new UserRole("FINANCE_ADMIN", "Finance Admin Role");
        role.setRoleId(2L);
        role.setCreatedDate(LocalDateTime.now());
        role.setUpdatedDate(LocalDateTime.now());
        return role;
    }

    public static UserMgnt createUserMgnt() {
        UserRole role = createUserRole();
        UserMgnt user = new UserMgnt(
                "John Doe",
                "john.doe@example.com",
                "encodedPassword",
                "IT",
                role
        );
        user.setUserId(1L);
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        return user;
    }

    public static UserProfileResponse createUserProfileResponse() {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(1L);
        response.setName("John Doe");
        response.setEmail("john.doe@example.com");
        response.setDepartment("IT");
        response.setRole("EMPLOYEE");
        response.setIsActive(true);
        response.setCreatedDate(LocalDateTime.now());
        response.setUpdatedDate(LocalDateTime.now());
        return response;
    }

    public static AuthenticationResponse createAuthenticationResponse() {
        UserProfileResponse userProfile = createUserProfileResponse();
        return new AuthenticationResponse("jwt-token", 3600000L, userProfile);
    }
}