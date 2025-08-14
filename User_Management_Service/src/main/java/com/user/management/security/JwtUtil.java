package com.user.management.security;

import com.user.management.constants.ApplicationConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT utility class for token generation, validation, and management
 * Integrates with Redis for token storage and blacklisting
 * 
 * @author User Management Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationConstants constants;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Constructor to initialize JWT utility
     * 
     * @param redisTemplate Redis template for token storage
     * @param constants Application constants
     */
    public JwtUtil(RedisTemplate<String, String> redisTemplate, ApplicationConstants constants) {
        this.redisTemplate = redisTemplate;
        this.constants = constants;
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Generate JWT token for user
     * 
     * @param email user's email address
     * @param role user's role
     * @return generated JWT token
     */
    public String generateToken(String email, String role) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + constants.JWT_TOKEN_VALIDITY);

            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

            // Store token in Redis with TTL
            storeTokenInRedis(email, token);
            
            log.info("JWT token generated successfully for user: {}", email);
            return token;
        } catch (Exception e) {
            log.error("Error generating JWT token for user: {}", email, e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Extract email from JWT token
     * 
     * @param token JWT token
     * @return user's email address
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Extract role from JWT token
     * 
     * @param token JWT token
     * @return user's role
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Error extracting role from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // First validate token structure and expiration
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            // Then check if token exists in Redis (not logged out)
            String email = getEmailFromToken(token);
            boolean isStoredInRedis = isTokenStoredInRedis(email, token);
            
            log.debug("Token validation - Email: {}, Stored in Redis: {}", email, isStoredInRedis);
            return isStoredInRedis;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired", e);
            return false;
        } catch (Exception e) {
            log.error("JWT token validation failed", e);
            return false;
        }
    }

    /**
     * Store JWT token in Redis with TTL
     * 
     * @param email user's email address
     * @param token JWT token
     */
    private void storeTokenInRedis(String email, String token) {
        try {
            String redisKey = constants.JWT_REDIS_PREFIX + email;
            redisTemplate.opsForValue().set(redisKey, token, constants.JWT_TOKEN_VALIDITY, TimeUnit.MILLISECONDS);
            log.debug("Token stored in Redis for user: {}", email);
        } catch (Exception e) {
            log.error("Error storing token in Redis for user: {}", email, e);
            throw new RuntimeException("Failed to store token in Redis", e);
        }
    }

    /**
     * Check if token is stored in Redis
     * 
     * @param email user's email address
     * @param token JWT token
     * @return true if token exists in Redis, false otherwise
     */
    private boolean isTokenStoredInRedis(String email, String token) {
        try {
            String redisKey = constants.JWT_REDIS_PREFIX + email;
            String storedToken = redisTemplate.opsForValue().get(redisKey);
            
            log.debug("Redis check - Key: {}, Stored token exists: {}, Tokens match: {}", 
                     redisKey, storedToken != null, token.equals(storedToken));
            
            return token.equals(storedToken);
        } catch (Exception e) {
            log.error("Error checking token in Redis for user: {}", email, e);
            return false;
        }
    }

    /**
     * Remove token from Redis (logout)
     * 
     * @param email user's email address
     */
    public void removeTokenFromRedis(String email) {
        try {
            String redisKey = constants.JWT_REDIS_PREFIX + email;
            redisTemplate.delete(redisKey);
            log.info("Token removed from Redis for user: {}", email);
        } catch (Exception e) {
            log.error("Error removing token from Redis for user: {}", email, e);
        }
    }

    /**
     * Get token expiration time
     * 
     * @return token expiration time in milliseconds
     */
    public long getTokenExpiration() {
        return constants.JWT_TOKEN_VALIDITY;
    }
}