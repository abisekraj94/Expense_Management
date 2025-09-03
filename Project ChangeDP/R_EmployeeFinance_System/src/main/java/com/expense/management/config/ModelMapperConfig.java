package com.expense.management.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for ModelMapper bean
 * Provides centralized configuration for object mapping throughout the application
 * 
 * @author System
 * @version 1.0.0
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and configures ModelMapper bean for object mapping
     * Uses strict matching strategy to ensure accurate field mapping
     * 
     * @return configured ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);
        return mapper;
    }

    /**
     * Creates RestTemplate bean for HTTP client operations
     * Used by CurrencyExchangeService for external API calls
     * 
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}