package com.user.management.config;

import com.user.management.constants.ApplicationConstants;
import com.user.management.entity.UserRole;
import com.user.management.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initialization component
 * Ensures required roles are created on application startup
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRoleRepository userRoleRepository;
    private final ApplicationConstants constants;

    /**
     * Initialize required data on application startup
     * 
     * @param args command line arguments
     */
    @Override
    public void run(String... args) {
        try {
            initializeRoles();
            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during data initialization", e);
            throw new RuntimeException("Failed to initialize application data", e);
        }
    }

    /**
     * Initialize user roles if they don't exist
     */
    private void initializeRoles() {
        // Create EMPLOYEE role if it doesn't exist
        if (!userRoleRepository.existsByRoleName(constants.ROLE_EMPLOYEE)) {
            UserRole employeeRole = new UserRole(
                constants.ROLE_EMPLOYEE,
                constants.EMPLOYEE_ROLE_DESCRIPTION
            );
            userRoleRepository.save(employeeRole);
            log.info("Created EMPLOYEE role");
        }

        // Create FINANCE_ADMIN role if it doesn't exist
        if (!userRoleRepository.existsByRoleName(constants.ROLE_FINANCE_ADMIN)) {
            UserRole financeAdminRole = new UserRole(
                constants.ROLE_FINANCE_ADMIN,
                constants.FINANCE_ADMIN_ROLE_DESCRIPTION
            );
            userRoleRepository.save(financeAdminRole);
            log.info("Created FINANCE_ADMIN role");
        }

        log.info("Role initialization completed");
    }
}