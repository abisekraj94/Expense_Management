package com.user.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.management.constants.AppErrorCodes;
import com.user.management.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Authentication Entry Point
 * Handles authentication failures and returns structured error response
 * 
 * @author User Management Team
 * @version 1.0
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle authentication failure
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authException the authentication exception
     * @throws IOException if response writing fails
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        log.error("Unauthorized access attempt: {}", authException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(AppErrorCodes.TOKEN_HAS_INVALID.getStatusCode().value());
        
        ApiResponse<Object> errorResponse = ApiResponse.error(
                AppErrorCodes.TOKEN_HAS_INVALID.getErrorMessage(), 
                AppErrorCodes.TOKEN_HAS_INVALID.getErrorCode()
        );
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}