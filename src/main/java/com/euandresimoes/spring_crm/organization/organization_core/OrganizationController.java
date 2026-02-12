package com.euandresimoes.spring_crm.organization.organization_core;

import com.euandresimoes.spring_crm.organization.organization_core.dto.CreateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.dto.UpdateOrganizationCommand;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ApiResponse<OrganizationResponse> createOrganization(
            @NonNull @AuthenticationPrincipal String userId,
            @RequestBody CreateOrganizationCommand command) {
        return ApiResponse.ok(organizationService.createOrganization(UUID.fromString(userId), command.name()));
    }

    @GetMapping("/find/all")
    public ApiResponse<List<OrganizationResponse>> findAll(@NonNull @AuthenticationPrincipal String userId) {
        return ApiResponse.ok(organizationService.findAllOrganizations(UUID.fromString(userId)));
    }

    @PutMapping
    public ApiResponse<Void> updateOrganization(
            @NonNull @AuthenticationPrincipal String userId,
            @Valid @RequestBody UpdateOrganizationCommand command) {
        organizationService.updateOrganization(UUID.fromString(userId), command);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrganization(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        organizationService.deleteOrganization(UUID.fromString(userId), id);
        return ApiResponse.ok(null);
    }
}
