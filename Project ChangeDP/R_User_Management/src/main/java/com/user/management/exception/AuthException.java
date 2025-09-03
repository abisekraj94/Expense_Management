package com.user.management.exception;

import com.user.management.constants.AppErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException {
    private String errorCode;
    private String errorMessage;
    private HttpStatus status;

    public AuthException(AppErrorCodes error) {
        super(error.getErrorMessage());
        this.errorCode = error.getErrorCode();
        this.errorMessage = error.getErrorMessage();
        this.status = error.getStatusCode();
    }
}
