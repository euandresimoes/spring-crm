package com.euandresimoes.spring_crm.organization.organization_core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// OrganizationEntity is now in the same package

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
    List<OrganizationEntity> findAllByUserId(UUID userId);

    Optional<OrganizationEntity> findByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
