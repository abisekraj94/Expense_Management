package com.user.management.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for Redis operations
 * Handles token storage and retrieval from Redis cache
 * 
 * @author User Management Team
 * @version 1.0
 */
@Component
@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String tokenPrefix;
    private final long tokenTtl;

    /**
     * Constructor to initialize Redis utility
     * 
     * @param redisTemplate the Redis template
     * @param tokenPrefix the token prefix from properties
     * @param tokenTtl the token TTL from properties
     */
    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate,
                     @Value("${auth.redis.token.prefix}") String tokenPrefix,
                     @Value("${auth.token.ttl}") long tokenTtl) {
        this.redisTemplate = redisTemplate;
        this.tokenPrefix = tokenPrefix;
        this.tokenTtl = tokenTtl;
    }

    /**
     * Store JWT token in Redis with TTL
     * 
     * @param userId the user ID
     * @param token the JWT token
     */
    public void storeToken(Long userId, String token) {
        try {
            String key = tokenPrefix + userId;
            redisTemplate.opsForValue().set(key, token, tokenTtl, TimeUnit.SECONDS);
            log.debug("Token stored in Redis for user: {}", userId);
        } catch (Exception e) {
            log.error("Error storing token in Redis for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Retrieve JWT token from Redis
     * 
     * @param userId the user ID
     * @return the JWT token if exists, null otherwise
     */
    public String getToken(Long userId) {
        try {
            String key = tokenPrefix + userId;
            Object token = redisTemplate.opsForValue().get(key);
            return token != null ? token.toString() : null;
        } catch (Exception e) {
            log.error("Error retrieving token from Redis for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Delete JWT token from Redis
     * 
     * @param userId the user ID
     */
    public void deleteToken(Long userId) {
        try {
            String key = tokenPrefix + userId;
            redisTemplate.delete(key);
            log.debug("Token deleted from Redis for user: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting token from Redis for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Check if token exists in Redis
     * 
     * @param userId the user ID
     * @return true if token exists, false otherwise
     */
    public boolean tokenExists(Long userId) {
        try {
            String key = tokenPrefix + userId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking token existence in Redis for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}