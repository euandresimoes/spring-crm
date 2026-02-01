package com.euandresimoes.spring_crm.auth.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("User not found, ID: " + id);
    }
}
