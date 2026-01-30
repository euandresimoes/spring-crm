package com.euandresimoes.spring_crm.shared.web.exception;

import com.euandresimoes.spring_crm.auth.domain.exception.EmailAlreadyInUseException;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneric(Exception ex) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error");
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ApiResponse<String> handleEmailInUse(EmailAlreadyInUseException ex) {
        return ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
}
