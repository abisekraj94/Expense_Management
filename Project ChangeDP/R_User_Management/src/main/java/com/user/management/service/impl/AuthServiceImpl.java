package com.user.management.service.impl;

import com.user.management.constants.AppErrorCodes;
import com.user.management.dto.response.AuthResponse;
import com.user.management.dto.request.LoginRequest;
import com.user.management.dto.request.RegisterRequest;
import com.user.management.dto.response.RegisteredResponse;
import com.user.management.entity.Employee;
import com.user.management.entity.EmployeeRoleMapping;
import com.user.management.entity.Role;
import com.user.management.exception.*;
import com.user.management.repository.DepartmentRepository;
import com.user.management.repository.EmployeeRepository;
import com.user.management.repository.EmployeeRoleMappingRepository;
import com.user.management.repository.RoleRepository;
import com.user.management.service.AuthService;
import com.user.management.util.JwtUtil;
import com.user.management.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AuthService interface
 * Handles user authentication operations with JWT and Redis integration
 * 
 * @author User Management Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRoleMappingRepository employeeRoleMappingRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ModelMapper modelMapper;
    private final FinanceServiceClient financeServiceClient;

    @Value("${role.employee}")
    private String defaultEmployeeRole;

    /**
     * Register a new user with role assignment
     * 
     * @param registerRequest the registration request data
     * @return authentication response with JWT token
     */
    @Override
    public RegisteredResponse register(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());
        try {
            if (employeeRepository.existsByEmailId(registerRequest.getEmail())) {
                log.warn("Registration failed - user already exists: {}", registerRequest.getEmail());
                throw new UserOperationException(AppErrorCodes.USER_ALREADY_EXIST);
            }

            Employee employee = modelMapper.map(registerRequest, Employee.class);
            employee.setEmailId(registerRequest.getEmail());
            employee.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            
            Employee savedEmployee = employeeRepository.save(employee);
            log.info("Employee saved with ID: {}", savedEmployee.getId());

            saveEmployeeRoleMapping(savedEmployee, registerRequest.getRoles());

            log.info("User registered successfully: {}", savedEmployee.getEmailId());

            return new RegisteredResponse(savedEmployee.getId());

        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw new RuntimeException("Registration failed", e);
        }
    }

    private void saveEmployeeRoleMapping(Employee employee, List<Long> roles) {
        List<EmployeeRoleMapping> employeeRoleMappings = roles
                .stream().map(roleId -> {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new UserOperationException(AppErrorCodes.ROLE_NOT_FOUND));
                    return new EmployeeRoleMapping(null, employee, role);
                }).toList();
        employeeRoleMappingRepository.saveAll(employeeRoleMappings);
        log.info("Employee role mappings has been saved properly");
    }

    /**
     * Authenticate user login
     * 
     * @param loginRequest the login request data
     * @return authentication response with JWT token
     */
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        try {
            Employee employee = employeeRepository.findByEmailId(loginRequest.getEmail())
                    .orElseThrow(() -> new UserOperationException(AppErrorCodes.USER_INVALID_CREDS));

            if (!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())) {
                log.error("Invalid password for user: {}", loginRequest.getEmail());
                throw new UserOperationException(AppErrorCodes.USER_INVALID_CREDS);
            }

            List<String> roles = employee.getEmployeeRoleMappings() != null ? 
                    employee.getEmployeeRoleMappings().stream()
                            .map(mapping -> mapping.getRole().getRoleName())
                            .collect(Collectors.toList()) : 
                    List.of();

            String token = jwtUtil.generateToken(employee.getId(), employee.getEmailId(), roles);

            redisUtil.storeToken(employee.getId(), token);

            // Send login notification to Finance service
            String primaryRole = roles.isEmpty() ? "EMPLOYEE" : roles.get(0);
            financeServiceClient.sendLoginNotification(
                employee.getId(),
                employee.getEmailId(),
                employee.getFirstName() + " " + employee.getLastName(),
                primaryRole
            );

            log.info("User logged in successfully: {}", employee.getEmailId());

            return new AuthResponse(
                    token,
                    employee.getId(),
                    employee.getEmailId(),
                    employee.getFirstName() + " " + employee.getLastName(),
                    roles
            );
        } catch (Exception exception) {
            if (exception instanceof UserOperationException)
                throw exception;
            log.error("Error during user login: {}", exception.getMessage(), exception);
            throw new RuntimeException("Login failed", exception);
        }
    }

    /**
     * Logout user and invalidate token
     * 
     * @param userId the user ID to logout
     */
    @Override
    public void logout(Long userId) {
        log.info("Logging out user: {}", userId);
        
        try {
            redisUtil.deleteToken(userId);
            log.info("User logged out successfully: {}", userId);
        } catch (Exception e) {
            log.error("Error during user logout: {}", e.getMessage(), e);
            throw new RuntimeException("Logout failed", e);
        }
    }

    /**
     * Validate if token exists in Redis
     * 
     * @param userId the user ID
     * @param token the JWT token
     * @return true if token is valid and exists in Redis
     */
    @Override
    public boolean validateTokenInRedis(Long userId, String token) {
        try {
            String redisToken = redisUtil.getToken(userId);
            return redisToken != null && redisToken.equals(token);
        } catch (Exception e) {
            log.error("Error validating token in Redis: {}", e.getMessage(), e);
            return false;
        }
    }
}