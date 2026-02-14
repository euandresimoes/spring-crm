package com.euandresimoes.spring_crm.organization.transactions.dto;

import com.euandresimoes.spring_crm.organization.transactions.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateTransactionCommand(
        @NotNull(message = "id is required") UUID id,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") BigDecimal amount,
        @NotNull(message = "Transaction type is required") TransactionType type) {
}
