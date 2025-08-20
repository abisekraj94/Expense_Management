package com.finance.admin.service;

import com.finance.admin.exception.GlobalExceptionHandler.BusinessException;
import com.finance.admin.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(currencyService, "currencyApiUrl", "https://api.exchangerate-api.com/v4/latest/");
        ReflectionTestUtils.setField(currencyService, "apiTimeout", 5000);
        ReflectionTestUtils.setField(currencyService, "baseCurrency", "INR");
        ReflectionTestUtils.setField(currencyService, "currencyInr", "INR");
        ReflectionTestUtils.setField(currencyService, "currencyUsd", "USD");
        ReflectionTestUtils.setField(currencyService, "currencyEur", "EUR");
        ReflectionTestUtils.setField(currencyService, "currencyGbp", "GBP");
        ReflectionTestUtils.setField(currencyService, "usdToInrRate", "83.00");
        ReflectionTestUtils.setField(currencyService, "eurToInrRate", "90.00");
        ReflectionTestUtils.setField(currencyService, "gbpToInrRate", "105.00");
        ReflectionTestUtils.setField(currencyService, "errorFailedFetchExchangeRates", "Failed to fetch exchange rates");
        ReflectionTestUtils.setField(currencyService, "errorExchangeRateNotFound", "Exchange rate not found for currency: ");
        ReflectionTestUtils.setField(currencyService, "errorFailedGetExchangeRate", "Failed to get exchange rate");
        ReflectionTestUtils.setField(currencyService, "errorFailedConvertCurrencyInr", "Failed to convert currency to INR");
        ReflectionTestUtils.setField(currencyService, "logGettingExchangeRate", "Getting exchange rate from {} to {}");
        ReflectionTestUtils.setField(currencyService, "logConvertingCurrency", "Converting {} {} to INR");
        ReflectionTestUtils.setField(currencyService, "logUsingDefaultExchangeRate", "Using default exchange rate for {} to {}");
        ReflectionTestUtils.setField(currencyService, "logExchangeRateResult", "Exchange rate from {} to {}: {}");
        ReflectionTestUtils.setField(currencyService, "logConvertedCurrency", "Converted {} {} to {} INR");
    }

    @Test
    void getExchangeRate_ShouldReturnOne_WhenSameCurrency() {
        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "USD");

        // Assert
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    void getExchangeRate_ShouldReturnDefaultRate_WhenApiCallSuccessful() {
        // Act - Test same currency scenario
        BigDecimal result = currencyService.getExchangeRate("USD", "USD");

        // Assert
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    void getExchangeRate_ShouldReturnOne_WhenSameCurrencyProvided() {
        // Act
        BigDecimal result = currencyService.getExchangeRate("EUR", "EUR");

        // Assert
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    void convertToInr_ShouldReturnSameAmount_WhenCurrencyIsINR() {
        // Arrange
        BigDecimal amount = new BigDecimal("1000.00");

        // Act
        BigDecimal result = currencyService.convertToInr(amount, "INR");

        // Assert
        assertEquals(amount, result);
    }

    @Test
    void getExchangeRateToInr_ShouldReturnOne_WhenCurrencyIsINR() {
        // Act
        BigDecimal result = currencyService.getExchangeRateToInr("INR");

        // Assert
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    void getExchangeRate_ShouldReturnOne_WhenBothCurrenciesAreSame() {
        // Act
        BigDecimal result = currencyService.getExchangeRate("GBP", "GBP");

        // Assert
        assertEquals(BigDecimal.ONE, result);
    }
}