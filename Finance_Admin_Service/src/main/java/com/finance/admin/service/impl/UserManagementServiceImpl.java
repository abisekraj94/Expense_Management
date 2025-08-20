package com.finance.admin.service.impl;

import com.finance.admin.dto.UserLoginRequest;
import com.finance.admin.dto.UserResponse;
import com.finance.admin.exception.AuthenticationException;
import com.finance.admin.exception.UserServiceException;
import com.finance.admin.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final RestTemplate restTemplate;
    
    @Value("${user.management.service.url:http://localhost:8080/api/v1}")
    private String userManagementUrl;
    
    @Value("${api.endpoint.auth.login}")
    private String authLoginEndpoint;
    
    @Value("${api.endpoint.users}")
    private String usersEndpoint;
    
    @Value("${log.authenticating.user}")
    private String logAuthenticatingUser;
    
    @Value("${log.fetching.user.by.id}")
    private String logFetchingUserById;
    
    @Value("${success.user.authenticated}")
    private String successUserAuthenticated;
    
    @Value("${success.user.fetched}")
    private String successUserFetched;
    
    @Value("${error.authentication.failed}")
    private String errorAuthenticationFailed;
    
    @Value("${error.failed.fetch.user}")
    private String errorFailedFetchUser;

    @Override
    public UserResponse authenticateUser(UserLoginRequest loginRequest) throws AuthenticationException {
        log.info(logAuthenticatingUser + "{}", loginRequest.getUsername());
        
        String url = userManagementUrl + authLoginEndpoint;
        
        try {
            UserResponse response = restTemplate.postForObject(url, loginRequest, UserResponse.class);
            log.info(successUserAuthenticated + "{}", loginRequest.getUsername());
            return response;
        } catch (Exception e) {
            log.error("Failed to authenticate user: {}", e.getMessage());
            throw new AuthenticationException(errorAuthenticationFailed + e.getMessage(), e);
        }
    }

    @Override
    public UserResponse getUserById(Long userId) throws UserServiceException {
        log.info(logFetchingUserById + "{}", userId);
        
        String url = userManagementUrl + usersEndpoint + userId;
        
        try {
            UserResponse response = restTemplate.getForObject(url, UserResponse.class);
            log.info(successUserFetched + "{}", userId);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch user: {}", e.getMessage());
            throw new UserServiceException(errorFailedFetchUser + e.getMessage(), e);
        }
    }
}