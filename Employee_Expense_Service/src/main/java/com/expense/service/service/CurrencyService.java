package com.expense.service.service;

import com.expense.service.exception.GlobalExceptionHandler.CurrencyConversionException;
import java.math.BigDecimal;

/**
 * Service interface for currency conversion operations
 * Handles currency rate fetching and conversion logic
 */
public interface CurrencyService {

    /**
     * Convert amount from source currency to target currency
     * @param amount Amount to convert
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @return Converted amount
     * @throws CurrencyConversionException if conversion fails
     */
    BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws CurrencyConversionException;

    /**
     * Get exchange rate between two currencies
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @return Exchange rate
     * @throws CurrencyConversionException if rate fetching fails
     */
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws CurrencyConversionException;
}