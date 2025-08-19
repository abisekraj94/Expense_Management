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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMgntRepository userMgntRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationConstants constants;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;
    private UserRole userRole;
    private UserMgnt userMgnt;
    private UserProfileResponse userProfileResponse;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "IT",
                "EMPLOYEE"
        );

        loginRequest = new UserLoginRequest(
                "john.doe@example.com",
                "password123"
        );

        userRole = new UserRole("EMPLOYEE", "Employee Role");
        userRole.setRoleId(1L);

        userMgnt = new UserMgnt(
                "John Doe",
                "john.doe@example.com",
                "encodedPassword",
                "IT",
                userRole
        );
        userMgnt.setUserId(1L);
        userMgnt.setCreatedDate(LocalDateTime.now());
        userMgnt.setUpdatedDate(LocalDateTime.now());

        userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUserId(1L);
        userProfileResponse.setName("John Doe");
        userProfileResponse.setEmail("john.doe@example.com");
        userProfileResponse.setDepartment("IT");
        userProfileResponse.setRole("EMPLOYEE");
        userProfileResponse.setIsActive(true);
    }

    @Test
    void testRegisterUser_Success() {
        when(userMgntRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRoleRepository.findByRoleName(anyString())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMgntRepository.save(any(UserMgnt.class))).thenReturn(userMgnt);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);

        UserProfileResponse result = userService.registerUser(registrationRequest);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userMgntRepository).save(any(UserMgnt.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userMgntRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(registrationRequest)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void testAuthenticateUser_Success() {
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(userMgnt));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(jwtUtil.getTokenExpiration()).thenReturn(3600000L);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);

        AuthenticationResponse result = userService.authenticateUser(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getAccessToken());
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.authenticateUser(loginRequest)
        );

        assertTrue(exception.getMessage().contains("Invalid credentials"));
    }

    @Test
    void testGetUserProfile_Success() {
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(userMgnt));
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);

        UserProfileResponse result = userService.getUserProfile("john.doe@example.com");

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUserProfile("nonexistent@example.com")
        );

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testLogoutUser_Success() {
        doNothing().when(jwtUtil).removeTokenFromRedis(anyString());

        assertDoesNotThrow(() -> userService.logoutUser("john.doe@example.com"));

        verify(jwtUtil).removeTokenFromRedis("john.doe@example.com");
    }
}