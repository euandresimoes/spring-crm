package com.euandresimoes.spring_crm.organization.transactions.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(UUID id) {
        super("Transaction not found with id: " + id);
    }
}
