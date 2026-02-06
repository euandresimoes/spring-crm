package com.euandresimoes.spring_crm.organization.organization_core.domain.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateOrganizationCommand(
        String userID,
        @NotBlank(message = "Organization name is required") @Length(min = 5, max = 15, message = "Organization name must be between 5 and 15 characters") String name) {

}
