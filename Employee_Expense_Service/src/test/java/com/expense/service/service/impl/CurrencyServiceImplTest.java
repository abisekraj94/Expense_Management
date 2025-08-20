package com.expense.service.service.impl;

import com.expense.service.constants.ApplicationConstants;
import com.expense.service.exception.GlobalExceptionHandler.CurrencyConversionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationConstants constants;

    @Mock
    private JsonNode jsonNode;

    @Mock
    private JsonNode ratesNode;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        constants.CURRENCY_CACHE_PREFIX = "currency_rate:";
        constants.CURRENCY_CACHE_TTL = 3600L;
        constants.CACHE_KEY_SEPARATOR = "_";
        constants.JSON_KEY_ERROR = "error";
        constants.JSON_KEY_RATES = "rates";
        constants.URL_SEPARATOR = "/";
        constants.EMPTY_CURRENCY_RESPONSE = "Empty response from currency API";
        constants.CURRENCY_API_ERROR = "Currency API error";
        constants.CURRENCY_RATE_NOT_FOUND = "Currency rate not found for";
        constants.INVALID_EXCHANGE_RATE = "Invalid exchange rate received";
        constants.CURRENCY_TIMEOUT = "Currency service timeout";
        constants.CURRENCY_UNAVAILABLE = "Currency service unavailable";
        constants.CURRENCY_CONVERSION_FAILED = "Currency conversion failed";

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void convertCurrency_SameCurrency_ReturnsOriginalAmount() throws CurrencyConversionException {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");

        // Act
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "USD");

        // Assert
        assertEquals(amount, result);
        verifyNoInteractions(redisTemplate, restTemplate);
    }

    @Test
    void convertCurrency_CachedRate_Success() throws CurrencyConversionException {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(valueOperations.get("currency_rate:USD_INR")).thenReturn("80.0");

        // Act
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "INR");

        // Assert
        assertEquals(new BigDecimal("8000.00"), result);
        verify(valueOperations).get("currency_rate:USD_INR");
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getExchangeRate_ApiCall_Success() throws Exception {
        // Arrange
        String apiResponse = "{\"rates\":{\"INR\":80.0}}";
        when(valueOperations.get(anyString())).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);
        when(objectMapper.readTree(apiResponse)).thenReturn(jsonNode);
        when(jsonNode.has("error")).thenReturn(false);
        when(jsonNode.get("rates")).thenReturn(ratesNode);
        when(ratesNode.has("INR")).thenReturn(true);
        when(ratesNode.get("INR")).thenReturn(jsonNode);
        when(jsonNode.decimalValue()).thenReturn(new BigDecimal("80.0"));

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "INR");

        // Assert
        assertEquals(new BigDecimal("80.0"), result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
        verify(valueOperations).set(anyString(), eq("80.0"), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    void getExchangeRate_EmptyResponse_ThrowsException() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("");

        // Act & Assert
        CurrencyConversionException exception = assertThrows(CurrencyConversionException.class,
            () -> currencyService.getExchangeRate("USD", "INR"));
        
        assertEquals("Empty response from currency API", exception.getMessage());
    }

    @Test
    void getExchangeRate_ApiError_ThrowsException() throws Exception {
        // Arrange
        String apiResponse = "{\"error\":\"Invalid API key\"}";
        when(valueOperations.get(anyString())).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);
        when(objectMapper.readTree(apiResponse)).thenReturn(jsonNode);
        when(jsonNode.has("error")).thenReturn(true);
        when(jsonNode.get("error")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("Invalid API key");

        // Act & Assert
        CurrencyConversionException exception = assertThrows(CurrencyConversionException.class,
            () -> currencyService.getExchangeRate("USD", "INR"));
        
        assertTrue(exception.getMessage().contains("Currency API error"));
    }

    @Test
    void getExchangeRate_CurrencyNotFound_ThrowsException() throws Exception {
        // Arrange
        String apiResponse = "{\"rates\":{\"EUR\":0.85}}";
        when(valueOperations.get(anyString())).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);
        when(objectMapper.readTree(apiResponse)).thenReturn(jsonNode);
        when(jsonNode.has("error")).thenReturn(false);
        when(jsonNode.get("rates")).thenReturn(ratesNode);
        when(ratesNode.has("INR")).thenReturn(false);

        // Act & Assert
        CurrencyConversionException exception = assertThrows(CurrencyConversionException.class,
            () -> currencyService.getExchangeRate("USD", "INR"));
        
        assertTrue(exception.getMessage().contains("Currency rate not found for"));
    }
}