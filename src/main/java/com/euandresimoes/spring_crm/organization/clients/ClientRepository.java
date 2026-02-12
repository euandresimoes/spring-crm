package com.euandresimoes.spring_crm.organization.clients;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    Page<ClientEntity> findAllByUserIdAndOrganization_Id(UUID userId, UUID organizationID, Pageable pageable);

    Optional<ClientEntity> findByIdAndUserIdAndOrganization_Id(UUID id, UUID userId, UUID organizationID);

    void deleteByIdAndUserIdAndOrganization_Id(UUID id, UUID userId, UUID organizationID);
}
