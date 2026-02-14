package com.euandresimoes.spring_crm.auth.dto;

import java.util.UUID;

import com.euandresimoes.spring_crm.auth.UserEntity;

import java.time.Instant;

public record CreateUserResponse(UUID id, String email, String role, Instant createdAt, Instant updatedAt) {

    public static CreateUserResponse from(UserEntity entity) {
        return new CreateUserResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
