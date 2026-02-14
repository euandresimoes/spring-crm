package com.euandresimoes.spring_crm.organization.organization_core.dto;

import java.time.Instant;
import java.util.UUID;

import com.euandresimoes.spring_crm.organization.organization_core.OrganizationEntity;

public record OrganizationResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt) {

    public OrganizationResponse from(OrganizationEntity entity) {
        return new OrganizationResponse(
                entity.getId(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
