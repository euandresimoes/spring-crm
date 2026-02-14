package com.euandresimoes.spring_crm.auth.dto;

import java.time.Instant;

import com.euandresimoes.spring_crm.auth.UserEntity;

public record ProfileResponse(
                String id,
                String email,
                String role,
                Instant createdAt,
                Instant updatedAt) {

        public static ProfileResponse from(UserEntity entity) {
                return new ProfileResponse(
                                entity.getId().toString(),
                                entity.getEmail(),
                                entity.getRole(),
                                entity.getCreatedAt(),
                                entity.getUpdatedAt());
        }

}
