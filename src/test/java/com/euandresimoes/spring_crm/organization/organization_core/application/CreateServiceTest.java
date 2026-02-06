package com.euandresimoes.spring_crm.organization.organization_core.application;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.CreateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the organization creation service.
 * Validates the creation of new organizations associated with a user.
 */
@ExtendWith(MockitoExtension.class)
class CreateServiceTest {

        @Mock
        private OrganizationRepository repository;

        @InjectMocks
        private CreateOrganizationService createService;

        @Test
        @DisplayName("Should create an organization successfully")
        void shouldCreateOrganizationSuccessfully() {
                // Arrange
                UUID userId = UUID.randomUUID();
                String userIdString = userId.toString();
                String organizationName = "Minha Empresa LTDA";

                CreateOrganizationCommand command = new CreateOrganizationCommand(
                                userIdString,
                                organizationName);

                // Create the entity that will be returned after the save
                UUID orgId = UUID.randomUUID();
                OrganizationEntity savedOrganization = new OrganizationEntity(
                                userId,
                                organizationName);
                savedOrganization.setId(orgId);

                when(repository.save(any(OrganizationEntity.class))).thenReturn(savedOrganization);

                // Act
                OrganizationResponse response = createService.execute(command);

                // Assert
                // Verify that the response contains the correct data
                assertThat(response).isNotNull();
                assertThat(response.id()).isEqualTo(orgId);
                assertThat(response.name()).isEqualTo(organizationName);

                // Verify that the repository was called
                verify(repository).save(any(OrganizationEntity.class));
        }

        @Test
        @DisplayName("Should associate the organization with the correct user")
        void shouldAssociateOrganizationWithCorrectUser() {
                // Arrange
                UUID userId = UUID.randomUUID();
                String userIdString = userId.toString();
                String organizationName = "Tech Solutions";

                CreateOrganizationCommand command = new CreateOrganizationCommand(
                                userIdString,
                                organizationName);

                OrganizationEntity savedOrganization = new OrganizationEntity(
                                userId,
                                organizationName);
                savedOrganization.setId(UUID.randomUUID());

                when(repository.save(any(OrganizationEntity.class))).thenReturn(savedOrganization);

                // Act
                createService.execute(command);

                // Assert
                // Verify that the organization was saved with the correct userID
                verify(repository).save(org.mockito.ArgumentMatchers
                                .argThat(org -> org.getUserID().equals(userId)
                                                && org.getName().equals(organizationName)));
        }

        @Test
        @DisplayName("Should correctly convert the userID from String to UUID")
        void shouldCorrectlyConvertUserId() {
                // Arrange
                UUID userId = UUID.randomUUID();
                String userIdString = userId.toString();
                String organizationName = "Startup XYZ";

                CreateOrganizationCommand command = new CreateOrganizationCommand(
                                userIdString,
                                organizationName);

                OrganizationEntity savedOrganization = new OrganizationEntity(
                                userId,
                                organizationName);
                when(repository.save(any(OrganizationEntity.class))).thenReturn(savedOrganization);

                // Act
                createService.execute(command);

                // Assert
                // Verify that the UUID was converted correctly
                verify(repository).save(org.mockito.ArgumentMatchers.argThat(org -> org.getUserID().equals(userId)));
        }

        @Test
        @DisplayName("Should create an organization with a name containing special characters")
        void shouldCreateOrganizationWithSpecialName() {
                // Arrange
                UUID userId = UUID.randomUUID();
                String specialCharactersName = "Empresa & Cia. - Soluções Técnicas (2024)";

                CreateOrganizationCommand command = new CreateOrganizationCommand(
                                userId.toString(),
                                specialCharactersName);

                UUID orgId = UUID.randomUUID();
                OrganizationEntity savedOrganization = new OrganizationEntity(
                                userId,
                                specialCharactersName);
                savedOrganization.setId(orgId);

                when(repository.save(any(OrganizationEntity.class))).thenReturn(savedOrganization);

                // Act
                OrganizationResponse response = createService.execute(command);

                // Assert
                // Verify that the name was correctly preserved
                assertThat(response.name()).isEqualTo(specialCharactersName);
        }

        @Test
        @DisplayName("Should return the ID generated by the database after saving")
        void shouldReturnIdGeneratedByDatabase() {
                // Arrange
                UUID userId = UUID.randomUUID();
                String organizationName = "Nova Organização";

                CreateOrganizationCommand command = new CreateOrganizationCommand(
                                userId.toString(),
                                organizationName);

                // Simulate that the database generated a specific ID
                UUID databaseGeneratedId = UUID.randomUUID();
                OrganizationEntity savedOrganization = new OrganizationEntity(
                                userId,
                                organizationName);
                savedOrganization.setId(databaseGeneratedId);

                when(repository.save(any(OrganizationEntity.class))).thenReturn(savedOrganization);

                // Act
                OrganizationResponse response = createService.execute(command);

                // Assert
                // Verify that the returned ID is the same as the one generated by the database
                assertThat(response.id()).isEqualTo(databaseGeneratedId);
        }
}
