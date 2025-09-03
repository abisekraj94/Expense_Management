package com.user.management.security;

import com.user.management.util.JwtUtil;
import com.user.management.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 * Processes JWT tokens and sets authentication context
 * 
 * @author User Management Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${auth.header.name}")
    private String authHeaderName;

    @Value("${auth.token.prefix}")
    private String tokenPrefix;

    /**
     * Filter JWT authentication for each request
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O operation fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                jwtUtil.validateToken(jwt);
                
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                String email = jwtUtil.getEmailFromToken(jwt);
                List<String> roles = jwtUtil.getRolesFromToken(jwt);
                
                // Check if token exists in Redis
                String redisToken = redisUtil.getToken(userId);
                if (redisToken != null && redisToken.equals(jwt)) {
                    // Create authorities from roles
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("Authentication set for user: {}", email);
                } else {
                    log.warn("Token not found in Redis or token mismatch for user: {}", userId);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from request header
     * 
     * @param request the HTTP request
     * @return JWT token if present, null otherwise
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(authHeaderName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
}