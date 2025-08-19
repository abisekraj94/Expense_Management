package com.expense.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application configuration class
 * Defines beans for RestTemplate, ModelMapper, and ObjectMapper
 */
@Configuration
public class AppConfig {

    /**
     * Creates RestTemplate bean for external API calls
     * Configures connection and read timeouts for reliable external service communication
     * 
     * @param builder RestTemplateBuilder for configuration
     * @return Configured RestTemplate with 5-second timeouts
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Creates ModelMapper bean for entity-DTO mapping
     * Provides automatic mapping between entity objects and DTOs
     * 
     * @return ModelMapper instance for object mapping
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

/*    *//**
     * Creates ObjectMapper bean for JSON processing
     * Handles JSON serialization and deserialization for API responses
     * 
     * @return ObjectMapper instance for JSON operations
     *//*
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }*/
}