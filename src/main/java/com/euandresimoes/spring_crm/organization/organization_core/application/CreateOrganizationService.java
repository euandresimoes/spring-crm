package com.euandresimoes.spring_crm.organization.organization_core.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.CreateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CreateOrganizationService {

    private final OrganizationRepository repo;

    public CreateOrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public OrganizationResponse execute(CreateOrganizationCommand command) {
        OrganizationEntity org = repo.save(new OrganizationEntity(
                UUID.fromString(command.userID()),
                command.name()));

        return new OrganizationResponse(org.getId(), org.getName());
    }
}
