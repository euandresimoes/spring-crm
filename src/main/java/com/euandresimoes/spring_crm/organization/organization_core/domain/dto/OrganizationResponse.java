package com.euandresimoes.spring_crm.organization.organization_core.domain.dto;

import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name) {

    public OrganizationResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
