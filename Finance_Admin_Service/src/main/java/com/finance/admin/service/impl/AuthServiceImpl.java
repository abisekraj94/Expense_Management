package com.finance.admin.service.impl;

import com.finance.admin.dto.FinanceAdminDto;
import com.finance.admin.dto.LoginRequestDto;
import com.finance.admin.dto.LoginResponseDto;
import com.finance.admin.entity.FinanceAdmin;
import com.finance.admin.exception.GlobalExceptionHandler.AuthenticationException;
import com.finance.admin.repository.FinanceAdminRepository;
import com.finance.admin.service.AuthService;
import com.finance.admin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService interface
 * Handles authentication and JWT token management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final FinanceAdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginResponseDto authenticateAdmin(LoginRequestDto loginRequest) {
        log.debug("Authenticating admin: {}", loginRequest.getUsername());
        
        try {
            // Find admin by username
            FinanceAdmin admin = adminRepository.findByUsernameAndIsDeletedFalse(loginRequest.getUsername())
                    .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
            
            // Check if admin is active
            if (!admin.getIsActive()) {
                throw new AuthenticationException("Admin account is inactive");
            }
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                throw new AuthenticationException("Invalid username or password");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(admin.getUsername());
            
            // Map admin to DTO
            FinanceAdminDto adminDto = modelMapper.map(admin, FinanceAdminDto.class);
            
            log.info("Admin authenticated successfully: {}", admin.getUsername());
            
            return LoginResponseDto.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpiration)
                    .admin(adminDto)
                    .build();
                    
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", loginRequest.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsernameFromToken(String token) {
        try {
            return jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw new AuthenticationException("Invalid token", e);
        }
    }
}