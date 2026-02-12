package com.euandresimoes.spring_crm.organization.organization_core;

import com.euandresimoes.spring_crm.organization.organization_core.dto.CreateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.dto.UpdateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private OrganizationService organizationService;

    @Test
    @DisplayName("Should create an organization successfully")
    void shouldCreateOrganizationSuccessfully() {
        UUID userId = UUID.randomUUID();
        String organizationName = "Minha Empresa LTDA";
        CreateOrganizationCommand command = new CreateOrganizationCommand(organizationName);

        OrganizationEntity savedOrganization = new OrganizationEntity(userId, organizationName);
        savedOrganization.setId(UUID.randomUUID());

        when(repository.save(any(OrganizationEntity.class))).thenReturn(savedOrganization);

        OrganizationResponse response = organizationService.createOrganization(userId, command.name());

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(savedOrganization.getId());
        assertThat(response.name()).isEqualTo(organizationName);
        verify(repository).save(any(OrganizationEntity.class));
    }

    @Test
    @DisplayName("Should delete an organization successfully")
    void shouldDeleteOrganizationSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        organizationService.deleteOrganization(userId, orgId);

        verify(repository).deleteByIdAndUserId(orgId, userId);
    }

    @Test
    @DisplayName("Should list all user's organizations successfully")
    void shouldListUserOrganizationsSuccessfully() {
        UUID userId = UUID.randomUUID();
        OrganizationEntity org1 = new OrganizationEntity(userId, "Company A");
        org1.setId(UUID.randomUUID());
        OrganizationEntity org2 = new OrganizationEntity(userId, "Company B");
        org2.setId(UUID.randomUUID());

        when(repository.findAllByUserId(userId)).thenReturn(Arrays.asList(org1, org2));

        List<OrganizationResponse> responses = organizationService.findAllOrganizations(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Company A");
        assertThat(responses.get(1).name()).isEqualTo("Company B");
        verify(repository).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Should update an organization successfully")
    void shouldUpdateOrganizationSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        String newName = "Updated Name";
        UpdateOrganizationCommand command = new UpdateOrganizationCommand(orgId, newName);

        OrganizationEntity organization = new OrganizationEntity(userId, "Old Name");
        organization.setId(orgId);

        when(repository.findByIdAndUserId(orgId, userId)).thenReturn(Optional.of(organization));
        when(repository.save(any(OrganizationEntity.class))).thenReturn(organization);

        organizationService.updateOrganization(userId, command);

        verify(repository).findByIdAndUserId(orgId, userId);
        verify(repository).save(argThat(org -> org.getName().equals(newName)));
    }

    @Test
    @DisplayName("Should throw OrganizationNotFoundException when the organization does not exist")
    void shouldThrowExceptionWhenOrganizationDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UpdateOrganizationCommand command = new UpdateOrganizationCommand(orgId, "New Name");

        when(repository.findByIdAndUserId(orgId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> organizationService.updateOrganization(userId, command))
                .isInstanceOf(OrganizationNotFoundException.class);
    }
}
