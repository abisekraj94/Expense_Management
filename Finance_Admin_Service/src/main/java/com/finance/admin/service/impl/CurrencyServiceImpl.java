package com.finance.admin.service.impl;

import com.finance.admin.exception.GlobalExceptionHandler.BusinessException;
import com.finance.admin.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Map;

/**
 * Implementation of CurrencyService interface
 * Handles currency conversion using external API
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final WebClient.Builder webClientBuilder;

    @Value("${currency.api.url}")
    private String currencyApiUrl;

    @Value("${currency.api.timeout:5000}")
    private int apiTimeout;

    @Value("${currency.base.currency:INR}")
    private String baseCurrency;

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        log.debug("Getting exchange rate from {} to {}", fromCurrency, toCurrency);
        
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(currencyApiUrl)
                    .build();
            
            // Get exchange rates with base currency
            ExchangeRateResponse response = webClient.get()
                    .uri(fromCurrency)
                    .retrieve()
                    .bodyToMono(ExchangeRateResponse.class)
                    .timeout(Duration.ofMillis(apiTimeout))
                    .block();
            
            if (response == null || response.getRates() == null) {
                throw new BusinessException("Failed to fetch exchange rates");
            }
            
            BigDecimal rate = response.getRates().get(toCurrency);
            if (rate == null) {
                throw new BusinessException("Exchange rate not found for currency: " + toCurrency);
            }
            
            log.debug("Exchange rate from {} to {}: {}", fromCurrency, toCurrency, rate);
            return rate.setScale(6, RoundingMode.HALF_UP);
            
        } catch (WebClientException e) {
            log.error("Error calling currency API: {}", e.getMessage());
            // Return default rate if API fails
            return getDefaultExchangeRate(fromCurrency, toCurrency);
        } catch (Exception e) {
            log.error("Error getting exchange rate: {}", e.getMessage(), e);
            throw new BusinessException("Failed to get exchange rate", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal convertToInr(BigDecimal amount, String fromCurrency) {
        log.debug("Converting {} {} to INR", amount, fromCurrency);
        
        if ("INR".equals(fromCurrency)) {
            return amount;
        }
        
        try {
            BigDecimal exchangeRate = getExchangeRateToInr(fromCurrency);
            BigDecimal convertedAmount = amount.multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);
            
            log.debug("Converted {} {} to {} INR", amount, fromCurrency, convertedAmount);
            return convertedAmount;
            
        } catch (Exception e) {
            log.error("Error converting to INR: {}", e.getMessage(), e);
            throw new BusinessException("Failed to convert currency to INR", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getExchangeRateToInr(String currency) {
        return getExchangeRate(currency, "INR");
    }

    /**
     * Get default exchange rate when API fails
     * 
     * @param fromCurrency source currency
     * @param toCurrency   target currency
     * @return default exchange rate
     */
    private BigDecimal getDefaultExchangeRate(String fromCurrency, String toCurrency) {
        log.warn("Using default exchange rate for {} to {}", fromCurrency, toCurrency);
        
        // Default rates from configuration
        if ("USD".equals(fromCurrency) && "INR".equals(toCurrency)) {
            return new BigDecimal("83.00");
        } else if ("EUR".equals(fromCurrency) && "INR".equals(toCurrency)) {
            return new BigDecimal("90.00");
        } else if ("GBP".equals(fromCurrency) && "INR".equals(toCurrency)) {
            return new BigDecimal("105.00");
        }
        
        // Default fallback rate
        return BigDecimal.ONE;
    }

    /**
     * Response class for exchange rate API
     */
    private static class ExchangeRateResponse {
        private String base;
        private String date;
        private Map<String, BigDecimal> rates;

        public String getBase() { return base; }
        public void setBase(String base) { this.base = base; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public Map<String, BigDecimal> getRates() { return rates; }
        public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
    }
}