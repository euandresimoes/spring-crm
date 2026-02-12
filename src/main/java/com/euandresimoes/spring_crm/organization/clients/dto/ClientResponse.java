package com.euandresimoes.spring_crm.organization.clients.dto;

import java.time.Instant;
import java.util.UUID;

import com.euandresimoes.spring_crm.organization.clients.ClientEntity;

public record ClientResponse(
        UUID id,
        UUID organization_id,
        UUID user_id,
        String name,
        String description,
        String email,
        String cpf_cnpj,
        String phone,
        String status,
        Instant created_at,
        Instant updated_at) {

    public static ClientResponse from(ClientEntity entity) {
        return new ClientResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getDescription(),
                entity.getEmail(),
                entity.getCpfCnpj(),
                entity.getPhone(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

}
