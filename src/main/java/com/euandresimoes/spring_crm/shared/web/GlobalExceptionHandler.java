package com.euandresimoes.spring_crm.shared.web;

import com.euandresimoes.spring_crm.auth.exception.AccountNotActiveException;
import com.euandresimoes.spring_crm.auth.exception.EmailAlreadyInUseException;
import com.euandresimoes.spring_crm.auth.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.exception.InvalidCredentialsException;
import com.euandresimoes.spring_crm.auth.exception.UserNotFoundException;
import com.euandresimoes.spring_crm.organization.clients.exception.ClientNotFoundException;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<String> handleUserNotFound(UserNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ApiResponse<String> handleOrganizationNotFound(OrganizationNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ApiResponse<String> handleClientNotFound(ClientNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
