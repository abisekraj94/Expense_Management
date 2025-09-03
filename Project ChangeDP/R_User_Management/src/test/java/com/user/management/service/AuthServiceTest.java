package com.user.management.service;

import com.user.management.dto.response.AuthResponse;
import com.user.management.dto.request.LoginRequest;
import com.user.management.dto.request.RegisterRequest;
import com.user.management.dto.response.RegisteredResponse;
import com.user.management.entity.Employee;
import com.user.management.entity.Role;
import com.user.management.exception.UserOperationException;
import com.user.management.repository.EmployeeRepository;
import com.user.management.repository.EmployeeRoleMappingRepository;
import com.user.management.repository.RoleRepository;
import com.user.management.service.impl.AuthServiceImpl;
import com.user.management.service.impl.FinanceServiceClient;
import com.user.management.util.JwtUtil;
import com.user.management.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * Tests authentication operations using JUnit and Mockito
 * 
 * @author User Management Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeRoleMappingRepository employeeRoleMappingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FinanceServiceClient financeServiceClient;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Employee employee;
    private Role role;

    /**
     * Set up test data before each test
     */
    @BeforeEach
    void setUp() {
        // Set up properties using ReflectionTestUtils
        ReflectionTestUtils.setField(authService, "defaultEmployeeRole", "EMPLOYEE");

        // Set up test data
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setMobileNo("1234567890");
        registerRequest.setPassword("password123");
        registerRequest.setRoles(List.of(1L, 2L, 3L));

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmailId("john.doe@example.com");
        employee.setPassword("encodedPassword");

        role = new Role();
        role.setId(1L);
        role.setRoleName("EMPLOYEE");
    }

    /**
     * Test successful user registration
     */
    @Test
    void testRegisterSuccess() {
        // Arrange
        when(employeeRepository.existsByEmailId(anyString())).thenReturn(false);
        when(modelMapper.map(any(RegisterRequest.class), eq(Employee.class))).thenReturn(employee);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(employeeRoleMappingRepository.saveAll(anyList())).thenReturn(null);

        // Act
        RegisteredResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
    }

    /**
     * Test registration failure when user already exists
     */
    @Test
    void testRegisterUserAlreadyExists() {
        // Arrange
        when(employeeRepository.existsByEmailId(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        verify(employeeRepository).existsByEmailId(registerRequest.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test successful user login
     */
    @Test
    void testLoginSuccess() {
        // Arrange
        when(employeeRepository.findByEmailId(anyString())).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyList())).thenReturn("jwt-token");
        doNothing().when(redisUtil).storeToken(anyLong(), anyString());
        doNothing().when(financeServiceClient).sendLoginNotification(anyLong(), anyString(), anyString(), anyString());

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(employee.getId(), response.getUserId());
        assertEquals(employee.getEmailId(), response.getEmail());
        
        verify(employeeRepository).findByEmailId(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), employee.getPassword());
        verify(redisUtil).storeToken(employee.getId(), "jwt-token");
    }

    /**
     * Test login failure with invalid credentials
     */
    @Test
    void testLoginInvalidCredentials() {
        // Arrange
        when(employeeRepository.findByEmailId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserOperationException.class, () -> {
            authService.login(loginRequest);
        });

        verify(employeeRepository).findByEmailId(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * Test login failure with wrong password
     */
    @Test
    void testLoginWrongPassword() {
        // Arrange
        when(employeeRepository.findByEmailId(anyString())).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(UserOperationException.class, () -> {
            authService.login(loginRequest);
        });

        verify(employeeRepository).findByEmailId(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), employee.getPassword());
    }

    /**
     * Test successful logout
     */
    @Test
    void testLogoutSuccess() {
        // Arrange
        Long userId = 1L;
        doNothing().when(redisUtil).deleteToken(anyLong());

        // Act
        assertDoesNotThrow(() -> {
            authService.logout(userId);
        });

        // Assert
        verify(redisUtil).deleteToken(userId);
    }

    /**
     * Test token validation in Redis
     */
    @Test
    void testValidateTokenInRedis() {
        // Arrange
        Long userId = 1L;
        String token = "jwt-token";
        when(redisUtil.getToken(anyLong())).thenReturn(token);

        // Act
        boolean isValid = authService.validateTokenInRedis(userId, token);

        // Assert
        assertTrue(isValid);
        verify(redisUtil).getToken(userId);
    }

    /**
     * Test token validation failure in Redis
     */
    @Test
    void testValidateTokenInRedisFailure() {
        // Arrange
        Long userId = 1L;
        String token = "jwt-token";
        String differentToken = "different-token";
        when(redisUtil.getToken(anyLong())).thenReturn(differentToken);

        // Act
        boolean isValid = authService.validateTokenInRedis(userId, token);

        // Assert
        assertFalse(isValid);
        verify(redisUtil).getToken(userId);
    }
}