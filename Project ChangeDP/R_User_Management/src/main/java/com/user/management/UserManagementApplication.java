package com.user.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;

/**
 * Main application class for User Management Microservice
 * Provides JWT authentication, role-based authorization, and Redis integration
 * 
 * @author User Management Team
 * @version 1.0
 */
@SpringBootApplication
@EnableFeignClients
@PropertySource("classpath:constant-application.properties")
public class UserManagementApplication {

	/**
	 * Main method to start the User Management application
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}
}