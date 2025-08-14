package com.expense.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Main application class for Employee Expense Service
 * Microservice for handling employee expense submissions and management
 */
@SpringBootApplication
@PropertySource("classpath:application-constants.properties")
public class EmployeeExpenseServiceApplication {

    /**
     * Main method to start the Employee Expense Service application
     * Initializes Spring Boot context and starts the embedded server
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(EmployeeExpenseServiceApplication.class, args);
    }
}