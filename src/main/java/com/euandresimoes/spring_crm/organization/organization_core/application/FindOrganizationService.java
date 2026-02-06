package com.euandresimoes.spring_crm.organization.organization_core.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

@Service
public class FindOrganizationService {

    private final OrganizationRepository repo;

    public FindOrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public List<OrganizationResponse> findAll(String userID) {
        List<OrganizationEntity> orgs = repo.findAllByUserID(UUID.fromString(userID));
        return orgs.stream().map(org -> new OrganizationResponse(org.getId(), org.getName())).toList();
    }

}
