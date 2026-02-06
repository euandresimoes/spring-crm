package com.euandresimoes.spring_crm.organization.organization_core.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.UpdateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.domain.exception.OrganizationNotFoundException;
import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UpdateOrganizationService {

    private final OrganizationRepository repo;

    public UpdateOrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public void execute(String userID, UpdateOrganizationCommand command) {
        OrganizationEntity org = repo.findByIdAndUserID(command.id(), UUID.fromString(userID))
                .orElseThrow(() -> new OrganizationNotFoundException(command.id()));

        org.setName(command.name());

        repo.save(org);
    }

}
