package com.finance.admin.service;

import com.finance.admin.dto.UserLoginRequest;
import com.finance.admin.dto.UserResponse;
import com.finance.admin.exception.AuthenticationException;
import com.finance.admin.exception.UserServiceException;
import com.finance.admin.service.impl.UserManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private UserLoginRequest loginRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userManagementService, "userManagementUrl", "http://localhost:8080/api/v1");
        ReflectionTestUtils.setField(userManagementService, "authLoginEndpoint", "/auth/login");
        ReflectionTestUtils.setField(userManagementService, "usersEndpoint", "/users/");
        ReflectionTestUtils.setField(userManagementService, "logAuthenticatingUser", "Authenticating user: ");
        ReflectionTestUtils.setField(userManagementService, "successUserAuthenticated", "User authenticated successfully: ");
        ReflectionTestUtils.setField(userManagementService, "successUserFetched", "User fetched successfully: ");
        ReflectionTestUtils.setField(userManagementService, "errorAuthenticationFailed", "Authentication failed: ");
        ReflectionTestUtils.setField(userManagementService, "errorFailedFetchUser", "Failed to fetch user: ");

        loginRequest = UserLoginRequest.builder()
                .username("admin")
                .password("password")
                .build();

        userResponse = UserResponse.builder()
                .userId(1L)
                .username("admin")
                .email("admin@company.com")
                .role("FINANCE_ADMIN")
                .active(true)
                .build();
    }

    @Test
    void authenticateUser_ShouldReturnUserResponse_WhenCredentialsValid() throws AuthenticationException {
        // Arrange
        when(restTemplate.postForObject(anyString(), eq(loginRequest), eq(UserResponse.class)))
                .thenReturn(userResponse);

        // Act
        UserResponse result = userManagementService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userResponse.getUserId(), result.getUserId());
        assertEquals(userResponse.getUsername(), result.getUsername());
        assertEquals(userResponse.getRole(), result.getRole());
        verify(restTemplate).postForObject(
                eq("http://localhost:8080/api/v1/auth/login"), 
                eq(loginRequest), 
                eq(UserResponse.class)
        );
    }

    @Test
    void authenticateUser_ShouldThrowAuthenticationException_WhenRestTemplateThrowsException() {
        // Arrange
        when(restTemplate.postForObject(anyString(), eq(loginRequest), eq(UserResponse.class)))
                .thenThrow(new RestClientException("Connection failed"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> userManagementService.authenticateUser(loginRequest));
        
        assertTrue(exception.getMessage().contains("Authentication failed"));
        verify(restTemplate).postForObject(anyString(), eq(loginRequest), eq(UserResponse.class));
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() throws UserServiceException {
        // Arrange
        Long userId = 1L;
        when(restTemplate.getForObject(anyString(), eq(UserResponse.class)))
                .thenReturn(userResponse);

        // Act
        UserResponse result = userManagementService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userResponse.getUserId(), result.getUserId());
        assertEquals(userResponse.getUsername(), result.getUsername());
        verify(restTemplate).getForObject(
                eq("http://localhost:8080/api/v1/users/1"), 
                eq(UserResponse.class)
        );
    }

    @Test
    void getUserById_ShouldThrowUserServiceException_WhenRestTemplateThrowsException() {
        // Arrange
        Long userId = 1L;
        when(restTemplate.getForObject(anyString(), eq(UserResponse.class)))
                .thenThrow(new RestClientException("User not found"));

        // Act & Assert
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userManagementService.getUserById(userId));
        
        assertTrue(exception.getMessage().contains("Failed to fetch user"));
        verify(restTemplate).getForObject(anyString(), eq(UserResponse.class));
    }
}