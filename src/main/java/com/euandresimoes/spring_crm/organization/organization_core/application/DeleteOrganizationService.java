package com.euandresimoes.spring_crm.organization.organization_core.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DeleteOrganizationService {

    private final OrganizationRepository repo;

    public DeleteOrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public void execute(String userID, UUID id) {
        repo.deleteByIdAndUserID(id, UUID.fromString(userID));
    }

}
