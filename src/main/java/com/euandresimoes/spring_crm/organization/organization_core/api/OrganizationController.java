package com.euandresimoes.spring_crm.organization.organization_core.api;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.euandresimoes.spring_crm.organization.organization_core.application.CreateOrganizationService;
import com.euandresimoes.spring_crm.organization.organization_core.application.DeleteOrganizationService;
import com.euandresimoes.spring_crm.organization.organization_core.application.FindOrganizationService;
import com.euandresimoes.spring_crm.organization.organization_core.application.UpdateOrganizationService;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.CreateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.UpdateOrganizationCommand;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;

@RestController
@RequestMapping("api/v1/organization")
public class OrganizationController {

    private final CreateOrganizationService createService;
    private final FindOrganizationService findService;
    private final DeleteOrganizationService deleteService;
    private final UpdateOrganizationService updateService;

    public OrganizationController(CreateOrganizationService createService, FindOrganizationService findService, DeleteOrganizationService deleteService,
            UpdateOrganizationService updateService) {
        this.createService = createService;
        this.findService = findService;
        this.deleteService = deleteService;
        this.updateService = updateService;
    }

    @PostMapping("/create/{name}")
    public ApiResponse<OrganizationResponse> create(@AuthenticationPrincipal String id,
            @PathVariable String name) {
        CreateOrganizationCommand command = new CreateOrganizationCommand(id, name);
        return ApiResponse.ok(createService.execute(command));
    }

    @GetMapping("/find")
    public ApiResponse<List<OrganizationResponse>> findAll(@AuthenticationPrincipal String id) {
        return ApiResponse.ok(findService.findAll(id));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal String userID, @PathVariable UUID id) {
        deleteService.execute(userID, id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/update")
    public ApiResponse<Void> update(@AuthenticationPrincipal String userID,
            @RequestBody UpdateOrganizationCommand command) {
        updateService.execute(userID, command);
        return ApiResponse.ok(null);
    }

}
