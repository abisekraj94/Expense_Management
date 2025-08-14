package com.finance.admin.service;

import java.math.BigDecimal;

/**
 * Service interface for currency conversion operations
 * Handles currency exchange rate fetching and conversion
 * 
 * @author Finance Team
 * @version 1.0.0
 */
public interface CurrencyService {

    /**
     * Get exchange rate from source currency to target currency
     * 
     * @param fromCurrency source currency code
     * @param toCurrency   target currency code
     * @return exchange rate
     */
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);

    /**
     * Convert amount from source currency to INR
     * 
     * @param amount       amount to convert
     * @param fromCurrency source currency code
     * @return converted amount in INR
     */
    BigDecimal convertToInr(BigDecimal amount, String fromCurrency);

    /**
     * Get current exchange rate to INR
     * 
     * @param currency currency code
     * @return exchange rate to INR
     */
    BigDecimal getExchangeRateToInr(String currency);
}