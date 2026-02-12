package com.euandresimoes.spring_crm.organization.organization_core.dto;

import java.util.UUID;

public record UpdateOrganizationCommand(
                UUID id,
                String name) {

}
