package com.finance.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for JPA
 * Enables JPA auditing and repository scanning
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.finance.admin.repository")
public class JpaConfig {
    // JPA configuration is handled by annotations
}