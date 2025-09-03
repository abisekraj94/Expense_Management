package com.expense.management.util;

import com.expense.management.constants.ApplicationConstants;
import com.expense.management.exception.ExternalApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * Service for currency exchange rate operations
 * Handles fetching exchange rates from external API and caching in Redis
 * 
 * @author System
 * @version 1.0.0
 */
@Service
@Slf4j
public class CurrencyExchangeService {

    @Value("${app.currency.api.base-url}")
    private String currencyApiBaseUrl;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for CurrencyExchangeService
     * 
     * @param redisTemplate Redis template for caching
     * @param restTemplate REST template for API calls
     * @param objectMapper Object mapper for JSON processing
     */
    public CurrencyExchangeService(RedisTemplate<String, Object> redisTemplate,
                                   RestTemplate restTemplate,
                                   ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Gets exchange rate from source currency to target currency
     * First checks Redis cache, if not found fetches from external API
     * 
     * @param fromCurrency source currency code
     * @param toCurrency target currency code
     * @return exchange rate
     * @throws ExternalApiException if API call fails
     */
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        log.debug("Getting exchange rate from {} to {}", fromCurrency, toCurrency);

        // If same currency, return 1
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        String cacheKey = ApplicationConstants.CACHE_CURRENCY_RATES + ":" + fromCurrency + "_" + toCurrency;
        
        try {
            // Check cache first
            String cachedRate = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cachedRate != null) {
                log.debug("Found cached exchange rate: {}", cachedRate);
                return new BigDecimal(cachedRate);
            }

            // Fetch from external API
            BigDecimal rate = fetchExchangeRateFromApi(fromCurrency, toCurrency);
            
            // Cache the result with TTL
            try {
                redisTemplate.opsForValue().set(cacheKey, rate.toString(), 
                        ApplicationConstants.CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                log.debug("Successfully cached exchange rate with key: {}, value: {}", cacheKey, rate);
                
                // Verify cache write
                Object verifyCache = redisTemplate.opsForValue().get(cacheKey);
                log.debug("Cache verification - key: {}, stored value: {}", cacheKey, verifyCache);
            } catch (Exception cacheException) {
                log.error("Failed to cache exchange rate: {}", cacheException.getMessage());
            }
            
            log.debug("Fetched and cached exchange rate: {}", rate);
            return rate;
            
        } catch (Exception e) {
            log.error("Error getting exchange rate from {} to {}: {}", fromCurrency, toCurrency, e.getMessage());
            throw new ExternalApiException("Failed to get exchange rate", e);
        }
    }

    /**
     * Tests Redis connection
     * 
     * @return true if Redis is connected
     */
    public boolean testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("test_key", "test_value", 10, TimeUnit.SECONDS);
            String result = (String) redisTemplate.opsForValue().get("test_key");
            redisTemplate.delete("test_key");
            log.info("Redis connection test: {}", result != null ? "SUCCESS" : "FAILED");
            return result != null;
        } catch (Exception e) {
            log.error("Redis connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Converts amount from source currency to target currency
     * 
     * @param amount amount to convert
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @return converted amount
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        log.debug("Converting {} {} to {}", amount, fromCurrency, toCurrency);
        
        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        BigDecimal convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        
        log.debug("Converted amount: {} (rate: {})", convertedAmount, exchangeRate);
        return convertedAmount;
    }

    /**
     * Fetches exchange rate from external API
     * 
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @return exchange rate
     * @throws ExternalApiException if API call fails
     */
    private BigDecimal fetchExchangeRateFromApi(String fromCurrency, String toCurrency) {
        try {
            String url = currencyApiBaseUrl + "/" + fromCurrency;
            log.debug("Calling currency API: {}", url);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Check if API response is successful
            if (!jsonNode.has("result") || !"success".equals(jsonNode.get("result").asText())) {
                throw new ExternalApiException("Currency API returned error response");
            }
            
            // Extract exchange rate
            JsonNode ratesNode = jsonNode.get("rates");
            if (!ratesNode.has(toCurrency)) {
                throw new ExternalApiException("Target currency not found in API response: " + toCurrency);
            }
            
            BigDecimal rate = new BigDecimal(ratesNode.get(toCurrency).asText());
            log.debug("API returned exchange rate: {}", rate);
            
            return rate;
            
        } catch (Exception e) {
            log.error("Failed to fetch exchange rate from API: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch exchange rate from external API", e);
        }
    }
}