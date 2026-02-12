package com.euandresimoes.spring_crm.organization.clients;

import com.euandresimoes.spring_crm.organization.clients.dto.ClientResponse;
import com.euandresimoes.spring_crm.organization.clients.dto.CreateClientCommand;
import com.euandresimoes.spring_crm.organization.clients.dto.UpdateClientCommand;
import com.euandresimoes.spring_crm.organization.organization_core.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.OrganizationRepository;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ClientService.
 * Validates business logic for creating, updating, deleting, and finding
 * clients.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepo;

    @Mock
    private OrganizationRepository organizationRepo;

    @InjectMocks
    private ClientService clientService;

    // --- Create Client Tests ---

    @Test
    @DisplayName("Should create a client successfully")
    void shouldCreateClientSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        CreateClientCommand command = new CreateClientCommand(
                "Client One",
                "Description test",
                "client@example.com",
                "12345678901",
                "11999999999",
                ClientStatus.ACTIVE);

        OrganizationEntity organization = new OrganizationEntity(userId, "Organization Name");
        organization.setId(orgId);

        when(organizationRepo.findByIdAndUserId(orgId, userId)).thenReturn(Optional.of(organization));

        // Act
        ClientResponse response = clientService.createClient(userId, orgId, command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(command.name());
        assertThat(response.email()).isEqualTo(command.email());

        verify(organizationRepo).findByIdAndUserId(orgId, userId);
        verify(clientRepo).save(any(ClientEntity.class));
    }

    @Test
    @DisplayName("Should throw OrganizationNotFoundException when creating a client for a non-existent organization")
    void shouldThrowExceptionWhenOrganizationNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        CreateClientCommand command = new CreateClientCommand(
                "Client One",
                "Description test",
                "client@example.com",
                "12345678901",
                "11999999999",
                ClientStatus.ACTIVE);

        when(organizationRepo.findByIdAndUserId(orgId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clientService.createClient(userId, orgId, command))
                .isInstanceOf(OrganizationNotFoundException.class);

        verify(organizationRepo).findByIdAndUserId(orgId, userId);
        verify(clientRepo, never()).save(any(ClientEntity.class));
    }

    // --- Delete Client Tests ---

    @Test
    @DisplayName("Should call repository to delete a client")
    void shouldDeleteClientSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        // Act
        clientService.deleteClient(userId, orgId, clientId);

        // Assert
        verify(clientRepo).deleteByIdAndUserIdAndOrganization_Id(clientId, userId, orgId);
    }

    // --- Find Clients Tests ---

    @Test
    @DisplayName("Should find clients with pagination")
    void shouldFindClientsWithPagination() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        OrganizationEntity organization = new OrganizationEntity(userId, "Organization Name");
        organization.setId(orgId);

        ClientEntity client = new ClientEntity(
                organization,
                userId,
                "Client One",
                "Description",
                "client@example.com",
                "12345678901",
                "11999999999",
                ClientStatus.ACTIVE);
        client.setId(UUID.randomUUID());
        client.setCreatedAt(Instant.now());
        client.setUpdatedAt(Instant.now());

        Page<ClientEntity> pageResult = new PageImpl<>(List.of(client));

        when(clientRepo.findAllByUserIdAndOrganization_Id(eq(userId), eq(orgId), any(Pageable.class)))
                .thenReturn(pageResult);

        // Act
        List<ClientResponse> responses = clientService.findClients(userId, orgId, page, size);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Client One");
        verify(clientRepo).findAllByUserIdAndOrganization_Id(eq(userId), eq(orgId), any(Pageable.class));
    }

    // --- Update Client Tests ---

    @Test
    @DisplayName("Should update a client successfully")
    void shouldUpdateClientSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UpdateClientCommand command = new UpdateClientCommand(
                clientId,
                "Updated Name",
                "Updated Description",
                "updated@example.com",
                "98765432100",
                "11888888888",
                ClientStatus.INACTIVE);

        OrganizationEntity organization = new OrganizationEntity(userId, "Organization Name");
        organization.setId(orgId);

        ClientEntity existingClient = new ClientEntity(
                organization,
                userId,
                "Old Name",
                "Old Description",
                "old@example.com",
                "12345678901",
                "11999999999",
                ClientStatus.ACTIVE);
        existingClient.setId(clientId);
        existingClient.setCreatedAt(Instant.now());
        existingClient.setUpdatedAt(Instant.now());

        when(clientRepo.findByIdAndUserIdAndOrganization_Id(clientId, userId, orgId))
                .thenReturn(Optional.of(existingClient));
        when(clientRepo.save(any(ClientEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ClientResponse response = clientService.updateClient(userId, orgId, command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Updated Name");
        assertThat(response.status()).isEqualTo("INACTIVE");

        verify(clientRepo).findByIdAndUserIdAndOrganization_Id(clientId, userId, orgId);
        verify(clientRepo).save(existingClient);
    }

    @Test
    @DisplayName("Should throw OrganizationNotFoundException when updating a non-existent client")
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UpdateClientCommand command = new UpdateClientCommand(
                clientId,
                "Updated Name",
                "Updated Description",
                "updated@example.com",
                "98765432100",
                "11888888888",
                ClientStatus.INACTIVE);

        when(clientRepo.findByIdAndUserIdAndOrganization_Id(clientId, userId, orgId))
                .thenReturn(Optional.empty());

        // Act & Assert
        // Note: Currently ClientService throws OrganizationNotFoundException even for
        // clients
        assertThatThrownBy(() -> clientService.updateClient(userId, orgId, command))
                .isInstanceOf(OrganizationNotFoundException.class);

        verify(clientRepo).findByIdAndUserIdAndOrganization_Id(clientId, userId, orgId);
        verify(clientRepo, never()).save(any(ClientEntity.class));
    }
}
