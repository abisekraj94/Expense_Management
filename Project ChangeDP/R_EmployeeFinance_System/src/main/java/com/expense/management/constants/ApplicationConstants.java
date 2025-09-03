package com.expense.management.constants;

/**
 * Application constants class containing all hardcoded values used across the application
 * This class centralizes all constant values to maintain consistency and ease of maintenance
 * 
 * @author System
 * @version 1.0.0
 */
public final class ApplicationConstants {

    // Expense Status Constants
    public static final String EXPENSE_STATUS_PENDING = "PENDING";
    public static final String EXPENSE_STATUS_APPROVED = "APPROVED";
    public static final String EXPENSE_STATUS_REJECTED = "REJECTED";

    // Currency Constants
    public static final String BASE_CURRENCY = "INR";

    // Cache Constants
    public static final String CACHE_CURRENCY_RATES = "currency_rates";
    public static final long CACHE_TTL_SECONDS = 3600L; // 1 hour

    // Pagination Constants
    public static final int MAX_PAGE_SIZE = 100;

    // Email Constants
    public static final String EMAIL_SUBJECT_APPROVAL = "Expense Approved - ";
    public static final String EMAIL_SUBJECT_REJECTION = "Expense Rejected - ";
    public static final String EMAIL_TEMPLATE_APPROVAL = "expense-approval";
    public static final String EMAIL_TEMPLATE_REJECTION = "expense-rejection";

    // Validation Constants
    public static final int DESCRIPTION_MAX_LENGTH = 500;
    public static final int AMOUNT_MAX_DIGITS = 10;
    public static final int AMOUNT_MAX_FRACTION = 2;
    public static final int CURRENCY_LENGTH = 3;
    public static final String CURRENCY_PATTERN = "^[A-Z]{3}$";

    // System Constants
    public static final String SYSTEM_USER = "SYSTEM";

    // API Endpoints
    public static final String API_BASE_PATH = "/api/v1";
    public static final String EXPENSE_API_PATH = API_BASE_PATH + "/expenses";
    public static final String ADMIN_API_PATH = API_BASE_PATH + "/admin";
    public static final String REPORT_API_PATH = API_BASE_PATH + "/reports";
}