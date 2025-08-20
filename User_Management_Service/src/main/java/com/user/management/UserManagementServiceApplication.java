package com.user.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for User Management Service
 * Provides JWT authentication, user registration, and profile management
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
public class UserManagementServiceApplication {

    /**
     * Main method to start the User Management Service application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }
}