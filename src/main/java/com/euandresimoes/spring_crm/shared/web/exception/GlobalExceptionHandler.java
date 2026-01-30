package com.euandresimoes.spring_crm.shared.web.exception;

import com.euandresimoes.spring_crm.auth.domain.exception.AccountNotActiveException;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailAlreadyInUseException;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.domain.exception.InvalidCredentialsException;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneric(Exception e) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ApiResponse<String> handleEmailInUse(EmailAlreadyInUseException e) {
        return ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ApiResponse<String> handleEmailNotFound(EmailNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ApiResponse<String> handleAccountNotActive(AccountNotActiveException e) {
        return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ApiResponse<String> handleInvalidCredentials(InvalidCredentialsException e) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }
}
