package com.euandresimoes.spring_crm.organization.organization_core.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateOrganizationCommand(
                @NotBlank(message = "Organization name is required") @Length(min = 5, max = 30, message = "Organization name must be between 5 and 30 characters") String name) {
}
