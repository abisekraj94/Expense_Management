package com.user.management.service;

import com.user.management.client.EmployeeExpenseClient;
import com.user.management.client.FinanceAdminClient;
import com.user.management.dto.ApiResponse;
import com.user.management.dto.EmployeeExpenseRequest;
import com.user.management.dto.FinanceAdminRequest;
import com.user.management.dto.UserProfileResponse;
import com.user.management.exception.MicroserviceCommunicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MicroserviceIntegrationServiceImplTest {

    @Mock
    private EmployeeExpenseClient employeeExpenseClient;

    @Mock
    private FinanceAdminClient financeAdminClient;

    @InjectMocks
    private MicroserviceIntegrationServiceImpl microserviceIntegrationService;

    private UserProfileResponse employeeUser;
    private UserProfileResponse financeAdminUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        employeeUser = new UserProfileResponse();
        employeeUser.setUserId(1L);
        employeeUser.setEmail("employee@example.com");
        employeeUser.setName("John Employee");
        employeeUser.setDepartment("IT");
        employeeUser.setRole("EMPLOYEE");

        financeAdminUser = new UserProfileResponse();
        financeAdminUser.setUserId(2L);
        financeAdminUser.setEmail("admin@example.com");
        financeAdminUser.setName("Jane Admin");
        financeAdminUser.setDepartment("Finance");
        financeAdminUser.setRole("FINANCE_ADMIN");

        testToken = "test-jwt-token";

        // Set @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(microserviceIntegrationService, "financeAdminRole", "FINANCE_ADMIN");
        ReflectionTestUtils.setField(microserviceIntegrationService, "tokenPrefix", "Bearer ");
        ReflectionTestUtils.setField(microserviceIntegrationService, "employeeNotificationSuccess", 
                "Successfully notified Employee Expense service for user: {}");
        ReflectionTestUtils.setField(microserviceIntegrationService, "financeNotificationSuccess", 
                "Successfully notified Finance Admin service for user: {}");
        ReflectionTestUtils.setField(microserviceIntegrationService, "employeeNotificationFailed", 
                "Failed to notify Employee Expense service for user: {}");
        ReflectionTestUtils.setField(microserviceIntegrationService, "financeNotificationFailed", 
                "Failed to notify Finance Admin service for user: {}");
    }

    @Test
    void testNotifyEmployeeExpenseService_Success() {
        when(employeeExpenseClient.registerEmployee(any(EmployeeExpenseRequest.class), anyString()))
                .thenReturn(new ApiResponse<>("SUCCESS", "Employee registered", "success", null));

        assertDoesNotThrow(() -> 
            microserviceIntegrationService.notifyEmployeeExpenseService(employeeUser, testToken)
        );

        verify(employeeExpenseClient).registerEmployee(any(EmployeeExpenseRequest.class), eq("Bearer " + testToken));
    }

    @Test
    void testNotifyEmployeeExpenseService_Failure() {
        doThrow(new RuntimeException("Service unavailable"))
                .when(employeeExpenseClient).registerEmployee(any(EmployeeExpenseRequest.class), anyString());

        MicroserviceCommunicationException exception = assertThrows(
                MicroserviceCommunicationException.class,
                () -> microserviceIntegrationService.notifyEmployeeExpenseService(employeeUser, testToken)
        );

        assertTrue(exception.getMessage().contains("Failed to notify Employee Expense service"));
        assertTrue(exception.getMessage().contains(employeeUser.getEmail()));
    }

    @Test
    void testNotifyFinanceAdminService_Success_ForFinanceAdmin() {
        when(financeAdminClient.registerAdmin(any(FinanceAdminRequest.class), anyString()))
                .thenReturn(new ApiResponse<>("SUCCESS", "Admin registered", "success", null));

        assertDoesNotThrow(() -> 
            microserviceIntegrationService.notifyFinanceAdminService(financeAdminUser, testToken)
        );

        verify(financeAdminClient).registerAdmin(any(FinanceAdminRequest.class), eq("Bearer " + testToken));
    }

    @Test
    void testNotifyFinanceAdminService_SkipForEmployee() {
        assertDoesNotThrow(() -> 
            microserviceIntegrationService.notifyFinanceAdminService(employeeUser, testToken)
        );

        verify(financeAdminClient, never()).registerAdmin(any(), anyString());
    }

    @Test
    void testNotifyFinanceAdminService_Failure() {
        doThrow(new RuntimeException("Service unavailable"))
                .when(financeAdminClient).registerAdmin(any(FinanceAdminRequest.class), anyString());

        MicroserviceCommunicationException exception = assertThrows(
                MicroserviceCommunicationException.class,
                () -> microserviceIntegrationService.notifyFinanceAdminService(financeAdminUser, testToken)
        );

        assertTrue(exception.getMessage().contains("Failed to notify Finance Admin service"));
        assertTrue(exception.getMessage().contains(financeAdminUser.getEmail()));
    }

    @Test
    void testNotifyEmployeeExpenseService_VerifyRequestMapping() {
        when(employeeExpenseClient.registerEmployee(any(EmployeeExpenseRequest.class), anyString()))
                .thenReturn(new ApiResponse<>("SUCCESS", "Employee registered", "success", null));

        microserviceIntegrationService.notifyEmployeeExpenseService(employeeUser, testToken);

        verify(employeeExpenseClient).registerEmployee(argThat(request -> 
            request.getUserId().equals(employeeUser.getUserId()) &&
            request.getUserEmail().equals(employeeUser.getEmail()) &&
            request.getUserName().equals(employeeUser.getName()) &&
            request.getDepartment().equals(employeeUser.getDepartment())
        ), eq("Bearer " + testToken));
    }

    @Test
    void testNotifyFinanceAdminService_VerifyRequestMapping() {
        when(financeAdminClient.registerAdmin(any(FinanceAdminRequest.class), anyString()))
                .thenReturn(new ApiResponse<>("SUCCESS", "Admin registered", "success", null));

        microserviceIntegrationService.notifyFinanceAdminService(financeAdminUser, testToken);

        verify(financeAdminClient).registerAdmin(argThat(request -> 
            request.getUserId().equals(financeAdminUser.getUserId()) &&
            request.getUserEmail().equals(financeAdminUser.getEmail()) &&
            request.getUserName().equals(financeAdminUser.getName()) &&
            request.getRole().equals(financeAdminUser.getRole())
        ), eq("Bearer " + testToken));
    }
}