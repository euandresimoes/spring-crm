package com.euandresimoes.spring_crm.organization.organization_core.exception;

import java.util.UUID;

public class OrganizationNotFoundException extends RuntimeException {
    public OrganizationNotFoundException(UUID id) {
        super("Organization not found with id: " + id);
    }
}
