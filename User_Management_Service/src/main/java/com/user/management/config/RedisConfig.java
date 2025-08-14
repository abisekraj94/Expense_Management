package com.user.management.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Redis
 * Provides Redis connection and template configuration
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    /**
     * Create Redis connection factory
     * 
     * @return Redis connection factory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(redisHost);
            config.setPort(redisPort);
            
            if (redisPassword != null && !redisPassword.isEmpty()) {
                config.setPassword(redisPassword);
            }
            
            log.info("Redis connection configured for host: {} port: {}", redisHost, redisPort);
            return new LettuceConnectionFactory(config);
        } catch (Exception e) {
            log.error("Error configuring Redis connection", e);
            throw new RuntimeException("Failed to configure Redis connection", e);
        }
    }

    /**
     * Create Redis template for string operations
     * 
     * @param connectionFactory Redis connection factory
     * @return configured Redis template
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            
            // Use String serializer for both keys and values
            StringRedisSerializer stringSerializer = new StringRedisSerializer();
            template.setKeySerializer(stringSerializer);
            template.setValueSerializer(stringSerializer);
            template.setHashKeySerializer(stringSerializer);
            template.setHashValueSerializer(stringSerializer);
            
            template.afterPropertiesSet();
            
            log.info("Redis template configured successfully");
            return template;
        } catch (Exception e) {
            log.error("Error configuring Redis template", e);
            throw new RuntimeException("Failed to configure Redis template", e);
        }
    }
}