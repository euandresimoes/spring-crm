package com.euandresimoes.spring_crm.auth.domain.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String email) {
        super("Account not active: " + email);
    }
}
