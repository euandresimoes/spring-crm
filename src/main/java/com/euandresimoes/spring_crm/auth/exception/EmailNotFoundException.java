package com.euandresimoes.spring_crm.auth.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("Email not found: " + email);
    }
}
