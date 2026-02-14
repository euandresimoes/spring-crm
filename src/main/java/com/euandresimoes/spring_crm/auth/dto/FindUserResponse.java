package com.euandresimoes.spring_crm.auth.dto;

import java.time.Instant;

import com.euandresimoes.spring_crm.auth.UserEntity;

public record FindUserResponse(
        String id,
        String email,
        String role,
        Instant createdAt,
        Instant updatedAt) {

    public static FindUserResponse from(UserEntity entity) {
        return new FindUserResponse(
                entity.getId().toString(),
                entity.getEmail(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
