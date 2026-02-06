package com.euandresimoes.spring_crm.organization.organization_core.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
    List<OrganizationEntity> findAllByUserID(UUID userID);

    Optional<OrganizationEntity> findByIdAndUserID(UUID id, UUID userID);

    void deleteByIdAndUserID(UUID id, UUID userID);
}
