package com.finance.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Finance Admin Service
 * Handles expense approval workflow for finance administrators
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class FinanceAdminApplication {

	/**
	 * Main method to start the Finance Admin Service application
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(FinanceAdminApplication.class, args);
	}
}
