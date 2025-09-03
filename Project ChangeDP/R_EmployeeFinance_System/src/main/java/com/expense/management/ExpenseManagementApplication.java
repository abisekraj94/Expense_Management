package com.expense.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Employee Expense Management System
 * This microservice handles employee expense submission and finance admin approval workflow
 * 
 * Features:
 * - Employee expense submission with currency conversion
 * - Finance admin approval/rejection workflow
 * - Redis caching for currency rates
 * - Email notifications
 * - Comprehensive reporting
 * 
 * @author System
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableFeignClients
public class ExpenseManagementApplication {

    /**
     * Main method to start the Spring Boot application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagementApplication.class, args);
    }
}