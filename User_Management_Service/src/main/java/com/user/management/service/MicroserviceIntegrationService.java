package com.user.management.service;

import com.user.management.dto.UserProfileResponse;
import com.user.management.exception.MicroserviceCommunicationException;

public interface MicroserviceIntegrationService {
    void notifyEmployeeExpenseService(UserProfileResponse user, String token) throws MicroserviceCommunicationException;
    void notifyFinanceAdminService(UserProfileResponse user, String token) throws MicroserviceCommunicationException;
}