package com.euandresimoes.spring_crm.organization.transactions.dto;

import com.euandresimoes.spring_crm.organization.transactions.TransactionEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String description,
        BigDecimal amount,
        String type,
        Instant createdAt,
        Instant updatedAt) {

    public static TransactionResponse from(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getType().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
