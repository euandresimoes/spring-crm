package com.euandresimoes.spring_crm.organization.organization_core;

import com.euandresimoes.spring_crm.organization.organization_core.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.dto.UpdateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrganizationService {

    private final OrganizationRepository repo;

    public OrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public OrganizationResponse createOrganization(UUID userId, String name) {
        OrganizationEntity org = repo.save(new OrganizationEntity(
                userId,
                name));

        return new OrganizationResponse(org.getId(), org.getName());
    }

    public void deleteOrganization(UUID userId, UUID id) {
        repo.deleteByIdAndUserId(id, userId);
    }

    public List<OrganizationResponse> findAllOrganizations(UUID userId) {
        List<OrganizationEntity> orgs = repo.findAllByUserId(userId);
        return orgs.stream().map(org -> new OrganizationResponse(org.getId(), org.getName())).toList();
    }

    public void updateOrganization(UUID userId, UpdateOrganizationCommand command) {
        OrganizationEntity org = repo.findByIdAndUserId(command.id(), userId)
                .orElseThrow(() -> new OrganizationNotFoundException(command.id()));

        org.setName(command.name());
        repo.save(org);
    }
}
