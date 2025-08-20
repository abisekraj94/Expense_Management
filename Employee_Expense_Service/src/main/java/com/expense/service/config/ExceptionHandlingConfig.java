package com.expense.service.config;

import com.expense.service.exception.GlobalExceptionHandler.ExternalServiceException;
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
            
            try {
                switch (statusCode) {
                    case NOT_FOUND:
                        throw new ExternalServiceException("External service endpoint not found");
                    case UNAUTHORIZED:
                        throw new ExternalServiceException("External service authentication failed");
                    case TOO_MANY_REQUESTS:
                        throw new ExternalServiceException("External service rate limit exceeded");
                    case INTERNAL_SERVER_ERROR:
                        throw new ExternalServiceException("External service internal error");
                    case SERVICE_UNAVAILABLE:
                        throw new ExternalServiceException("External service unavailable");
                    default:
                        throw new ExternalServiceException("External service error: " + statusCode);
                }
            } catch (ExternalServiceException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}