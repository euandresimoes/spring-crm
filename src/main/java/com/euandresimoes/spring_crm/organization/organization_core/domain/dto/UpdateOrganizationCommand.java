package com.euandresimoes.spring_crm.organization.organization_core.domain.dto;

import java.util.UUID;

public record UpdateOrganizationCommand(
        UUID id,
        String name) {

}
