package com.expense.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.time.Duration;

/**
 * Configuration for exception handling and external service resilience
 */
@Configuration
@Slf4j
public class ExceptionHandlingConfig {

    /**
     * Configures RestTemplate with timeout and error handling
     */
/*    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .errorHandler(new CustomResponseErrorHandler())
                .build();
    }*/

    /**
     * Custom error handler for RestTemplate
     */
    private static class CustomResponseErrorHandler implements ResponseErrorHandler {
        
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().is4xxClientError() || 
                   response.getStatusCode().is5xxServerError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode = (HttpStatus) response.getStatusCode();
            log.error("External API error: {} - {}", statusCode, response.getStatusText());
            
            switch (statusCode) {
                case NOT_FOUND:
                    throw new RuntimeException("External service endpoint not found");
                case UNAUTHORIZED:
                    throw new RuntimeException("External service authentication failed");
                case TOO_MANY_REQUESTS:
                    throw new RuntimeException("External service rate limit exceeded");
                case INTERNAL_SERVER_ERROR:
                    throw new RuntimeException("External service internal error");
                case SERVICE_UNAVAILABLE:
                    throw new RuntimeException("External service unavailable");
                default:
                    throw new RuntimeException("External service error: " + statusCode);
            }
        }
    }
}