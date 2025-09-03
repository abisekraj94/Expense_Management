package com.user.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.management.constants.AppErrorCodes;
import com.user.management.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        
        log.error("Access denied: {}", accessDeniedException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(AppErrorCodes.ACCESS_DENIED.getStatusCode().value());
        
        ApiResponse<Object> errorResponse = ApiResponse.error(
                AppErrorCodes.ACCESS_DENIED.getErrorMessage(), 
                AppErrorCodes.ACCESS_DENIED.getErrorCode()
        );
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}