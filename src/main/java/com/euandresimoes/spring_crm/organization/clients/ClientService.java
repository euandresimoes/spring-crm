package com.euandresimoes.spring_crm.organization.clients;

import com.euandresimoes.spring_crm.organization.clients.dto.ClientResponse;
import com.euandresimoes.spring_crm.organization.clients.dto.CreateClientCommand;
import com.euandresimoes.spring_crm.organization.clients.dto.UpdateClientCommand;
import com.euandresimoes.spring_crm.organization.clients.exception.ClientNotFoundException;
import com.euandresimoes.spring_crm.organization.organization_core.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.OrganizationRepository;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepo;
    private final OrganizationRepository organizationRepo;

    public ClientService(ClientRepository clientRepo, OrganizationRepository organizationRepo) {
        this.clientRepo = clientRepo;
        this.organizationRepo = organizationRepo;
    }

    public ClientResponse createClient(UUID userId, UUID organizationID, CreateClientCommand command) {
        OrganizationEntity org = organizationRepo.findByIdAndUserId(organizationID, userId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationID));

        ClientEntity client = new ClientEntity(
                org,
                userId,
                command.name(),
                command.description(),
                command.email(),
                command.cpf_cnpj(),
                command.phone(),
                command.status());

        clientRepo.save(client);
        return ClientResponse.from(client);
    }

    public void deleteClient(UUID userId, UUID organizationID, UUID id) {
        clientRepo.deleteByIdAndUserIdAndOrganization_Id(
                id,
                userId,
                organizationID);
    }

    public List<ClientResponse> findClients(UUID userId, UUID organizationID, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClientEntity> pageResult = clientRepo.findAllByUserIdAndOrganization_Id(
                userId,
                organizationID,
                pageable);

        return pageResult
                .getContent()
                .stream()
                .map(ClientResponse::from)
                .toList();
    }

    public ClientResponse updateClient(UUID userId, UUID organizationID, UpdateClientCommand command) {
        ClientEntity client = clientRepo.findByIdAndUserIdAndOrganization_Id(
                command.id(),
                userId,
                organizationID)
                .orElseThrow(() -> new ClientNotFoundException(command.id()));
        client.setName(command.name());
        client.setDescription(command.description());
        client.setEmail(command.email());
        client.setCpfCnpj(command.cpf_cnpj());
        client.setPhone(command.phone());
        client.setStatus(command.status());

        return ClientResponse.from(clientRepo.save(client));
    }
}
