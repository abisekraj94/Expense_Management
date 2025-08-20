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
    
    @Value("${currency.inr}")
    private String currencyInr;
    
    @Value("${currency.usd}")
    private String currencyUsd;
    
    @Value("${currency.eur}")
    private String currencyEur;
    
    @Value("${currency.gbp}")
    private String currencyGbp;
    
    @Value("${exchange.rate.usd.to.inr}")
    private String usdToInrRate;
    
    @Value("${exchange.rate.eur.to.inr}")
    private String eurToInrRate;
    
    @Value("${exchange.rate.gbp.to.inr}")
    private String gbpToInrRate;
    
    @Value("${error.failed.fetch.exchange.rates}")
    private String errorFailedFetchExchangeRates;
    
    @Value("${error.exchange.rate.not.found}")
    private String errorExchangeRateNotFound;
    
    @Value("${error.failed.get.exchange.rate}")
    private String errorFailedGetExchangeRate;
    
    @Value("${error.failed.convert.currency.inr}")
    private String errorFailedConvertCurrencyInr;
    
    @Value("${log.getting.exchange.rate}")
    private String logGettingExchangeRate;
    
    @Value("${log.converting.currency}")
    private String logConvertingCurrency;
    
    @Value("${log.using.default.exchange.rate}")
    private String logUsingDefaultExchangeRate;
    
    @Value("${log.exchange.rate.result}")
    private String logExchangeRateResult;
    
    @Value("${log.converted.currency}")
    private String logConvertedCurrency;

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        log.debug(logGettingExchangeRate, fromCurrency, toCurrency);
        
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
                throw new BusinessException(errorFailedFetchExchangeRates);
            }
            
            BigDecimal rate = response.getRates().get(toCurrency);
            if (rate == null) {
                throw new BusinessException(errorExchangeRateNotFound + toCurrency);
            }
            
            log.debug(logExchangeRateResult, fromCurrency, toCurrency, rate);
            return rate.setScale(6, RoundingMode.HALF_UP);
            
        } catch (WebClientException e) {
            log.error("Error calling currency API: {}", e.getMessage());
            // Return default rate if API fails
            return getDefaultExchangeRate(fromCurrency, toCurrency);
        } catch (Exception e) {
            log.error("Error getting exchange rate: {}", e.getMessage(), e);
            throw new BusinessException(errorFailedGetExchangeRate, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal convertToInr(BigDecimal amount, String fromCurrency) {
        log.debug(logConvertingCurrency, amount, fromCurrency);
        
        if (currencyInr.equals(fromCurrency)) {
            return amount;
        }
        
        try {
            BigDecimal exchangeRate = getExchangeRateToInr(fromCurrency);
            BigDecimal convertedAmount = amount.multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);
            
            log.debug(logConvertedCurrency, amount, fromCurrency, convertedAmount);
            return convertedAmount;
            
        } catch (Exception e) {
            log.error("Error converting to INR: {}", e.getMessage(), e);
            throw new BusinessException(errorFailedConvertCurrencyInr, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getExchangeRateToInr(String currency) {
        return getExchangeRate(currency, currencyInr);
    }

    /**
     * Get default exchange rate when API fails
     * 
     * @param fromCurrency source currency
     * @param toCurrency   target currency
     * @return default exchange rate
     */
    private BigDecimal getDefaultExchangeRate(String fromCurrency, String toCurrency) {
        log.warn(logUsingDefaultExchangeRate, fromCurrency, toCurrency);
        
        // Default rates from configuration
        if (currencyUsd.equals(fromCurrency) && currencyInr.equals(toCurrency)) {
            return new BigDecimal(usdToInrRate);
        } else if (currencyEur.equals(fromCurrency) && currencyInr.equals(toCurrency)) {
            return new BigDecimal(eurToInrRate);
        } else if (currencyGbp.equals(fromCurrency) && currencyInr.equals(toCurrency)) {
            return new BigDecimal(gbpToInrRate);
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