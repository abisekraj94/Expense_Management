package com.expense.service.service.impl;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.exception.GlobalExceptionHandler.CurrencyConversionException;
import com.expense.service.service.CurrencyService;
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
 * Implementation of CurrencyService
 * Handles currency conversion with Redis caching and external API integration
 */
@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationConstants constants;

    @Value("${currency.api.url}")
    private String currencyApiUrl;

    /**
     * Constructor for CurrencyServiceImpl
     * Initializes all required dependencies for currency conversion operations
     * 
     * @param restTemplate Template for making HTTP requests to external APIs
     * @param redisTemplate Template for Redis cache operations
     * @param objectMapper JSON object mapper for parsing API responses
     * @param constants Application constants containing configuration values
     */
    public CurrencyServiceImpl(RestTemplate restTemplate, RedisTemplate<String, String> redisTemplate, 
                              ObjectMapper objectMapper, ApplicationConstants constants) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.constants = constants;
    }

    /**
     * Converts currency amount from one currency to another
     * Uses cached exchange rates when available, otherwise fetches from external API
     * 
     * @param amount The amount to convert
     * @param fromCurrency Source currency code (e.g., "USD")
     * @param toCurrency Target currency code (e.g., "INR")
     * @return Converted amount rounded to 2 decimal places
     * @throws CurrencyConversionException if conversion fails
     */
    @Override
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        log.info("Converting {} {} to {}", amount, fromCurrency, toCurrency);
        
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        BigDecimal convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        
        log.info("Converted amount: {} {}", convertedAmount, toCurrency);
        return convertedAmount;
    }

    /**
     * Retrieves exchange rate between two currencies
     * First checks Redis cache, then fetches from external API if not cached
     * Caches the fetched rate for 1 hour to improve performance
     * 
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @return Exchange rate as BigDecimal
     * @throws CurrencyConversionException if rate cannot be fetched or parsed
     */
    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        String cacheKey = constants.CURRENCY_CACHE_PREFIX + fromCurrency + "_" + toCurrency;
        
        // Try to get from cache first
        String cachedRate = redisTemplate.opsForValue().get(cacheKey);
        if (cachedRate != null) {
            log.info("Using cached exchange rate for {} to {}: {}", fromCurrency, toCurrency, cachedRate);
            return new BigDecimal(cachedRate);
        }

        // Fetch from external API
        try {
            String url = currencyApiUrl + "/" + fromCurrency;
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode ratesNode = jsonNode.get("rates");
            
            if (ratesNode == null || !ratesNode.has(toCurrency)) {
                throw new CurrencyConversionException("Currency rate not found for " + toCurrency);
            }
            
            BigDecimal rate = ratesNode.get(toCurrency).decimalValue();
            
            // Cache the rate for 1 hour
            redisTemplate.opsForValue().set(cacheKey, rate.toString(), constants.CURRENCY_CACHE_TTL, TimeUnit.SECONDS);
            
            log.info("Fetched and cached exchange rate for {} to {}: {}", fromCurrency, toCurrency, rate);
            return rate;
            
        } catch (Exception e) {
            log.error("Failed to fetch currency rate for {} to {}: {}", fromCurrency, toCurrency, e.getMessage());
            throw new CurrencyConversionException(constants.CURRENCY_CONVERSION_FAILED, e);
        }
    }
}