package com.user.management.service;

import com.user.management.client.EmployeeExpenseClient;
import com.user.management.client.FinanceAdminClient;
import com.user.management.dto.EmployeeExpenseRequest;
import com.user.management.dto.FinanceAdminRequest;
import com.user.management.dto.UserProfileResponse;
import com.user.management.exception.MicroserviceCommunicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MicroserviceIntegrationServiceImpl implements MicroserviceIntegrationService {
    
    private final EmployeeExpenseClient employeeExpenseClient;
    private final FinanceAdminClient financeAdminClient;
    
    @Value("${user.role.finance.admin}")
    private String financeAdminRole;
    
    @Value("${jwt.token.prefix}")
    private String tokenPrefix;
    
    @Value("${microservice.notification.success.employee}")
    private String employeeNotificationSuccess;
    
    @Value("${microservice.notification.success.finance}")
    private String financeNotificationSuccess;
    
    @Value("${microservice.notification.failed.employee}")
    private String employeeNotificationFailed;
    
    @Value("${microservice.notification.failed.finance}")
    private String financeNotificationFailed;
    
    @Override
    public void notifyEmployeeExpenseService(UserProfileResponse user, String token) throws MicroserviceCommunicationException {
        try {
            EmployeeExpenseRequest request = new EmployeeExpenseRequest(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getDepartment()
            );
            employeeExpenseClient.registerEmployee(request, tokenPrefix + token);
            log.info(employeeNotificationSuccess, user.getEmail());
        } catch (Exception e) {
            log.error(employeeNotificationFailed, user.getEmail(), e);
            throw new MicroserviceCommunicationException("Failed to notify Employee Expense service for user: " + user.getEmail(), e);
        }
    }
    
    @Override
    public void notifyFinanceAdminService(UserProfileResponse user, String token) throws MicroserviceCommunicationException {
        try {
            if (financeAdminRole.equals(user.getRole())) {
                FinanceAdminRequest request = new FinanceAdminRequest(
                    user.getUserId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRole()
                );
                financeAdminClient.registerAdmin(request, tokenPrefix + token);
                log.info(financeNotificationSuccess, user.getEmail());
            }
        } catch (Exception e) {
            log.error(financeNotificationFailed, user.getEmail(), e);
            throw new MicroserviceCommunicationException("Failed to notify Finance Admin service for user: " + user.getEmail(), e);
        }
    }
}