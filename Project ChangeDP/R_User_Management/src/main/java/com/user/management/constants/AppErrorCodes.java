package com.user.management.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppErrorCodes {
    USER_INVALID_CREDS("USER-001", "Invalid credentials", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_EXIST("USER-002", "User already exist", HttpStatus.ACCEPTED),
    USER_NOT_FOUND("USER-003", "User not found", HttpStatus.ACCEPTED),
    ROLE_NOT_FOUND("ROLE-001", "Role not found", HttpStatus.ACCEPTED),
    TOKEN_HAS_EXPIRED("TOK-001","Token has expired",HttpStatus.UNAUTHORIZED),
    TOKEN_HAS_INVALID("TOK-002","Invalid token",HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("TOK-003","Access denied",HttpStatus.UNAUTHORIZED);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus statusCode;

    AppErrorCodes(String errorCode, String errorMessage, HttpStatus statusCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
}
