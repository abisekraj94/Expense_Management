package com.finance.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.admin.dto.UserLoginRequest;
import com.finance.admin.dto.UserResponse;
import com.finance.admin.exception.AuthenticationException;
import com.finance.admin.exception.UserServiceException;
import com.finance.admin.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserLoginRequest loginRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
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
    void login_ShouldReturnUserResponse_WhenValidCredentials() throws Exception {
        // Arrange
        when(userManagementService.authenticateUser(any(UserLoginRequest.class)))
                .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("FINANCE_ADMIN"));
    }

    @Test
    void login_ShouldReturnForbidden_WhenUserNotFinanceAdmin() throws Exception {
        // Arrange
        userResponse.setRole("EMPLOYEE");
        when(userManagementService.authenticateUser(any(UserLoginRequest.class)))
                .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenAuthenticationFails() throws Exception {
        // Arrange
        when(userManagementService.authenticateUser(any(UserLoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Arrange
        UserLoginRequest invalidRequest = UserLoginRequest.builder()
                .username("")
                .password("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() throws Exception {
        // Arrange
        when(userManagementService.getUserById(1L)).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(get("/auth/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void getUserById_ShouldReturnServiceUnavailable_WhenServiceFails() throws Exception {
        // Arrange
        when(userManagementService.getUserById(1L))
                .thenThrow(new UserServiceException("Service unavailable"));

        // Act & Assert
        mockMvc.perform(get("/auth/user/1"))
                .andExpect(status().isServiceUnavailable());
    }
}