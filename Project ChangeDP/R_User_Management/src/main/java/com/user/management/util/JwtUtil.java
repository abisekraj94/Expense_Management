package com.user.management.util;

import com.user.management.constants.AppErrorCodes;
import com.user.management.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Utility class for JWT token operations
 * Handles token generation, validation, and extraction of claims
 * 
 * @author User Management Team
 * @version 1.0
 */
@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final long jwtExpiration;

    /**
     * Constructor to initialize JWT utility with secret and expiration
     * 
     * @param secret the JWT secret key
     * @param expiration the JWT expiration time in milliseconds
     */
    public JwtUtil(@Value("${jwt.secret}") String secret, 
                   @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = expiration;
    }

    /**
     * Generate JWT token for user
     * 
     * @param userId the user ID
     * @param email the user email
     * @param roles the user roles
     * @return generated JWT token
     */
    public String generateToken(Long userId, String email, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract email from JWT token
     * 
     * @param token the JWT token
     * @return email from token
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extract user ID from JWT token
     * 
     * @param token the JWT token
     * @return user ID from token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract roles from JWT token
     * 
     * @param token the JWT token
     * @return roles from token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (List<String>) claims.get("roles");
    }

    /**
     * Validate JWT token
     * 
     * @param token the JWT token to validate
     * @throws AuthException if token is invalid or expired
     */
    public void validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims.getExpiration().before(new Date())) {
                throw new AuthException(AppErrorCodes.TOKEN_HAS_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            throw new AuthException(AppErrorCodes.TOKEN_HAS_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new AuthException(AppErrorCodes.TOKEN_HAS_INVALID);
        }
    }



    /**
     * Extract claims from JWT token
     * 
     * @param token the JWT token
     * @return claims from token
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}