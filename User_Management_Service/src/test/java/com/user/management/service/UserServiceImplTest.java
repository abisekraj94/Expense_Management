package com.user.management.service;

import com.user.management.constants.ApplicationConstants;
import com.user.management.dto.*;
import com.user.management.entity.UserMgnt;
import com.user.management.entity.UserRole;
import com.user.management.exception.*;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    private MicroserviceIntegrationService microserviceIntegrationService;

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
        
        // Set the @Value field using ReflectionTestUtils
        ReflectionTestUtils.setField(userService, "registrationNotificationFailed", 
                "Failed to notify microservices for user: {}");
    }

    @Test
    void testRegisterUser_Success() {
        when(userMgntRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRoleRepository.findByRoleName(anyString())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMgntRepository.save(any(UserMgnt.class))).thenReturn(userMgnt);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("temp-token");
        doNothing().when(microserviceIntegrationService).notifyEmployeeExpenseService(any(), anyString());
        doNothing().when(microserviceIntegrationService).notifyFinanceAdminService(any(), anyString());

        UserProfileResponse result = userService.registerUser(registrationRequest);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userMgntRepository).save(any(UserMgnt.class));
        verify(microserviceIntegrationService).notifyEmployeeExpenseService(any(), anyString());
        verify(microserviceIntegrationService).notifyFinanceAdminService(any(), anyString());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userMgntRepository.existsByEmail(anyString())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(registrationRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userMgntRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_RoleNotFound() {
        when(userMgntRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRoleRepository.findByRoleName(anyString())).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> userService.registerUser(registrationRequest)
        );

        assertTrue(exception.getMessage().contains("Role not found"));
        verify(userMgntRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_MicroserviceNotificationFails() {
        when(userMgntRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRoleRepository.findByRoleName(anyString())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMgntRepository.save(any(UserMgnt.class))).thenReturn(userMgnt);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("temp-token");
        doThrow(new MicroserviceCommunicationException("Service unavailable"))
                .when(microserviceIntegrationService).notifyEmployeeExpenseService(any(), anyString());

        UserProfileResponse result = userService.registerUser(registrationRequest);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userMgntRepository).save(any(UserMgnt.class));
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
    void testAuthenticateUser_UserNotFound() {
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.authenticateUser(loginRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(userMgnt));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.authenticateUser(loginRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
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

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserProfile("nonexistent@example.com")
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateUserProfile_Success() {
        UserRegistrationRequest updateRequest = new UserRegistrationRequest(
                "John Updated", "john.doe@example.com", "newPassword", "HR", "EMPLOYEE"
        );
        
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(userMgnt));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userMgntRepository.save(any(UserMgnt.class))).thenReturn(userMgnt);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);

        UserProfileResponse result = userService.updateUserProfile("john.doe@example.com", updateRequest);

        assertNotNull(result);
        verify(userMgntRepository).save(any(UserMgnt.class));
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        UserRegistrationRequest updateRequest = new UserRegistrationRequest(
                "John Updated", "john.doe@example.com", "newPassword", "HR", "EMPLOYEE"
        );
        
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserProfile("john.doe@example.com", updateRequest)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateUserProfile_RoleNotFound() {
        UserRegistrationRequest updateRequest = new UserRegistrationRequest(
                "John Updated", "john.doe@example.com", "newPassword", "HR", "INVALID_ROLE"
        );
        
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(userMgnt));
        when(userRoleRepository.findByRoleName("INVALID_ROLE")).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> userService.updateUserProfile("john.doe@example.com", updateRequest)
        );

        assertTrue(exception.getMessage().contains("Role not found"));
    }

    @Test
    void testUpdateUserProfile_WithoutPasswordChange() {
        UserRegistrationRequest updateRequest = new UserRegistrationRequest(
                "John Updated", "john.doe@example.com", null, "HR", "EMPLOYEE"
        );
        
        when(userMgntRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(userMgnt));
        when(userMgntRepository.save(any(UserMgnt.class))).thenReturn(userMgnt);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);

        UserProfileResponse result = userService.updateUserProfile("john.doe@example.com", updateRequest);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testGetAllEmployees_Success() {
        List<UserMgnt> employees = Arrays.asList(userMgnt, userMgnt);
        when(userMgntRepository.findByIsActiveTrueOrderByCreatedDateDesc()).thenReturn(employees);
        when(modelMapper.map(any(), eq(UserProfileResponse.class))).thenReturn(userProfileResponse);

        List<UserProfileResponse> result = userService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userMgntRepository).findByIsActiveTrueOrderByCreatedDateDesc();
    }

    @Test
    void testGetAllEmployees_EmptyList() {
        when(userMgntRepository.findByIsActiveTrueOrderByCreatedDateDesc()).thenReturn(Arrays.asList());

        List<UserProfileResponse> result = userService.getAllEmployees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testLogoutUser_Success() {
        doNothing().when(jwtUtil).removeTokenFromRedis(anyString());

        assertDoesNotThrow(() -> userService.logoutUser("john.doe@example.com"));

        verify(jwtUtil).removeTokenFromRedis("john.doe@example.com");
    }

    @Test
    void testLogoutUser_RedisException() {
        doThrow(new RuntimeException("Redis connection failed"))
                .when(jwtUtil).removeTokenFromRedis(anyString());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.logoutUser("john.doe@example.com")
        );

        assertEquals("Redis connection failed", exception.getMessage());
    }
}