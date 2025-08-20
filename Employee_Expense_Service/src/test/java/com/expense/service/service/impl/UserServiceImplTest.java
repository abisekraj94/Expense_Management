package com.expense.service.service.impl;

import com.expense.service.client.UserManagementClient;
import com.expense.service.constants.ApplicationConstants;
import com.expense.service.dto.User;
import com.expense.service.exception.GlobalExceptionHandler.UserServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserManagementClient userManagementClient;

    @Mock
    private ApplicationConstants constants;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");

        constants.USER_NOT_FOUND = "User not found";
    }

    @Test
    void getUserById_Success() throws UserServiceException {
        // Arrange
        when(userManagementClient.getUserById(1L)).thenReturn(user);

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userManagementClient).getUserById(1L);
    }

    @Test
    void getUserById_ClientError_ThrowsException() {
        // Arrange
        when(userManagementClient.getUserById(1L))
            .thenThrow(new RuntimeException("Client error"));

        // Act & Assert
        UserServiceException exception = assertThrows(UserServiceException.class,
            () -> userService.getUserById(1L));
        
        assertEquals("User not found", exception.getMessage());
        verify(userManagementClient).getUserById(1L);
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        // Arrange
        when(userManagementClient.getUserById(999L))
            .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        UserServiceException exception = assertThrows(UserServiceException.class,
            () -> userService.getUserById(999L));
        
        assertEquals("User not found", exception.getMessage());
        verify(userManagementClient).getUserById(999L);
    }
}