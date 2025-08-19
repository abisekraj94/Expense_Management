package com.finance.admin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Constants utility class for accessing configuration values
 * Centralizes all hardcoded values from properties file
 * 
 * @author Finance Team
 * @version 1.0.0
 */
public class Constants {

    // Application Constants
    @Value("${app.name}")
    public static String APP_NAME;

    @Value("${app.version}")
    public static String APP_VERSION;

    @Value("${app.author}")
    public static String APP_AUTHOR;

    // JWT Constants
    @Value("${jwt.token.type}")
    public static String JWT_TOKEN_TYPE;

    // Currency Constants
    @Value("${currency.base.currency}")
    public static String BASE_CURRENCY;

    @Value("${currency.default.usd.rate}")
    public static String DEFAULT_USD_RATE;

    @Value("${currency.default.eur.rate}")
    public static String DEFAULT_EUR_RATE;

    @Value("${currency.default.gbp.rate}")
    public static String DEFAULT_GBP_RATE;

    @Value("${default.currency.inr}")
    public static String CURRENCY_INR;

    @Value("${default.currency.usd}")
    public static String CURRENCY_USD;

    @Value("${default.currency.eur}")
    public static String CURRENCY_EUR;

    @Value("${default.currency.gbp}")
    public static String CURRENCY_GBP;

    // Expense Status Constants
    @Value("${expense.status.pending}")
    public static String STATUS_PENDING;

    @Value("${expense.status.approved}")
    public static String STATUS_APPROVED;

    @Value("${expense.status.rejected}")
    public static String STATUS_REJECTED;

    // Audit Constants
    @Value("${audit.created.by}")
    public static String AUDIT_SYSTEM_USER;

    // Security Constants
    @Value("${security.role.finance.admin}")
    public static String ROLE_FINANCE_ADMIN;

    @Value("${security.role.prefix}")
    public static String ROLE_PREFIX;

    @Value("${default.admin.role}")
    public static String DEFAULT_ADMIN_ROLE;

    // Default Values
    @Value("${default.admin.active}")
    public static Boolean DEFAULT_ADMIN_ACTIVE;

    @Value("${default.entity.deleted}")
    public static Boolean DEFAULT_ENTITY_DELETED;

    @Value("${default.exchange.rate}")
    public static String DEFAULT_EXCHANGE_RATE;

    // Email Constants
    @Value("${email.subject.approval}")
    public static String EMAIL_SUBJECT_APPROVAL;

    @Value("${email.subject.rejection}")
    public static String EMAIL_SUBJECT_REJECTION;

    @Value("${email.company.name}")
    public static String COMPANY_NAME;

    // Error Messages
    @Value("${error.resource.not.found}")
    public static String ERROR_RESOURCE_NOT_FOUND;

    @Value("${error.business.rule.violation}")
    public static String ERROR_BUSINESS_RULE_VIOLATION;

    @Value("${error.authentication.failed}")
    public static String ERROR_AUTHENTICATION_FAILED;

    @Value("${error.expense.not.pending}")
    public static String ERROR_EXPENSE_NOT_PENDING;

    @Value("${error.admin.account.inactive}")
    public static String ERROR_ADMIN_ACCOUNT_INACTIVE;

    @Value("${error.invalid.credentials}")
    public static String ERROR_INVALID_CREDENTIALS;

    @Value("${error.invalid.token}")
    public static String ERROR_INVALID_TOKEN;

    // Success Messages
    @Value("${success.expense.approved}")
    public static String SUCCESS_EXPENSE_APPROVED;

    @Value("${success.expense.rejected}")
    public static String SUCCESS_EXPENSE_REJECTED;

    @Value("${success.login.successful}")
    public static String SUCCESS_LOGIN_SUCCESSFUL;

    @Value("${success.email.sent}")
    public static String SUCCESS_EMAIL_SENT;

    // Field Lengths
    @Value("${field.length.username.min}")
    public static Integer USERNAME_MIN_LENGTH;

    @Value("${field.length.username.max}")
    public static Integer USERNAME_MAX_LENGTH;

    @Value("${field.length.password.min}")
    public static Integer PASSWORD_MIN_LENGTH;

    @Value("${field.length.name.max}")
    public static Integer NAME_MAX_LENGTH;

    @Value("${field.length.email.max}")
    public static Integer EMAIL_MAX_LENGTH;

    @Value("${field.length.description.max}")
    public static Integer DESCRIPTION_MAX_LENGTH;

    @Value("${field.length.currency}")
    public static Integer CURRENCY_LENGTH;

    @Value("${field.length.rejection.reason.max}")
    public static Integer REJECTION_REASON_MAX_LENGTH;


}