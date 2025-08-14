package com.finance.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient
 * Configures HTTP client for external API calls
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Configuration
public class WebClientConfig {

    /**
     * Configure WebClient builder for HTTP calls
     * 
     * @return WebClient builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024));
    }
}