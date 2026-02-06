package com.euandresimoes.spring_crm.organization.clients.infra.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.euandresimoes.spring_crm.organization.clients.domain.ClientEntity;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

}
