package com.euandresimoes.spring_crm.organization.organization_core.application;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.UpdateOrganizationCommand;
import com.euandresimoes.spring_crm.organization.organization_core.domain.exception.OrganizationNotFoundException;
import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the organization update service.
 * Validates the update of existing organizations and access control by
 * user.
 */
@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {

        @Mock
        private OrganizationRepository repository;

        @InjectMocks
        private UpdateOrganizationService updateService;

        @Test
        @DisplayName("Should update an organization successfully")
        void shouldUpdateOrganizationSuccessfully() {
                // Arrange
                UUID userId = UUID.randomUUID();
                UUID orgId = UUID.randomUUID();
                String oldName = "Old Name";
                String newName = "Updated Name";
                UpdateOrganizationCommand command = new UpdateOrganizationCommand(
                                orgId,
                                newName);

                OrganizationEntity organization = new OrganizationEntity(
                                userId,
                                oldName);
                organization.setId(orgId);

                // Simulate that the organization was found
                when(repository.findByIdAndUserID(orgId, userId)).thenReturn(Optional.of(organization));
                when(repository.save(any(OrganizationEntity.class))).thenReturn(organization);

                // Act
                updateService.execute(userId.toString(), command);

                // Assert
                // Verify that the repository was consulted
                verify(repository).findByIdAndUserID(orgId, userId);
                // Verify that the save method was called
                verify(repository).save(organization);
                // Verify that the name was updated
                verify(repository).save(argThat(org -> org.getName().equals(newName)));
        }

        @Test
        @DisplayName("Should throw OrganizationNotFoundException when the organization does not exist")
        void shouldThrowExceptionWhenOrganizationDoesNotExist() {
                // Arrange
                UUID userId = UUID.randomUUID();
                UUID orgId = UUID.randomUUID();

                UpdateOrganizationCommand command = new UpdateOrganizationCommand(
                                orgId,
                                "New Name");

                // Simulate that the organization was not found
                when(repository.findByIdAndUserID(orgId, userId)).thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> updateService.execute(userId.toString(), command))
                                .isInstanceOf(OrganizationNotFoundException.class);

                // Verify that the repository was consulted
                verify(repository).findByIdAndUserID(orgId, userId);
                // Verify that the save method was never called
                verify(repository, never()).save(any(OrganizationEntity.class));
        }

        @Test
        @DisplayName("Should prevent updating an organization belonging to another user")
        void shouldPreventUpdatingOrganizationOfAnotherUser() {
                // Arrange
                UUID userId1 = UUID.randomUUID();
                UUID userId2 = UUID.randomUUID();
                UUID orgId = UUID.randomUUID();

                UpdateOrganizationCommand command = new UpdateOrganizationCommand(
                                orgId,
                                "Attempted Update");

                // Simulate that the organization was not found for userId2
                // (because it belongs to userId1)
                when(repository.findByIdAndUserID(orgId, userId2)).thenReturn(Optional.empty());

                // Act & Assert
                // Attempt to update with userId2, but the organization belongs to userId1
                assertThatThrownBy(() -> updateService.execute(userId2.toString(), command))
                                .isInstanceOf(OrganizationNotFoundException.class);

                // Verify that the save method was never called
                verify(repository, never()).save(any(OrganizationEntity.class));
        }

        @Test
        @DisplayName("Should correctly convert the userID from String to UUID")
        void shouldCorrectlyConvertUserId() {
                // Arrange
                UUID userId = UUID.randomUUID();
                String userIdString = userId.toString();
                UUID orgId = UUID.randomUUID();

                UpdateOrganizationCommand command = new UpdateOrganizationCommand(
                                orgId,
                                "Updated Name");

                OrganizationEntity organization = new OrganizationEntity(userId, "Old Name");
                organization.setId(orgId);

                when(repository.findByIdAndUserID(orgId, userId)).thenReturn(Optional.of(organization));
                when(repository.save(any(OrganizationEntity.class))).thenReturn(organization);

                // Act
                updateService.execute(userIdString, command);

                // Assert
                // Verify that the UUID was converted correctly
                verify(repository).findByIdAndUserID(orgId, userId);
        }

        @Test
        @DisplayName("Should update the name with special characters")
        void shouldUpdateNameWithSpecialCharacters() {
                // Arrange
                UUID userId = UUID.randomUUID();
                UUID orgId = UUID.randomUUID();
                String specialCharactersName = "Empresa & Cia. - Soluções (2024)";
                UpdateOrganizationCommand command = new UpdateOrganizationCommand(
                                orgId,
                                specialCharactersName);

                OrganizationEntity organization = new OrganizationEntity(userId, "Old Name");
                organization.setId(orgId);

                when(repository.findByIdAndUserID(orgId, userId)).thenReturn(Optional.of(organization));
                when(repository.save(any(OrganizationEntity.class))).thenReturn(organization);

                // Act
                updateService.execute(userId.toString(), command);

                // Assert
                // Verify that the name with special characters was saved correctly
                verify(repository).save(argThat(org -> org.getName().equals(specialCharactersName)));
        }

        @Test
        @DisplayName("Should keep the same ID and userID after an update")
        void shouldKeepIdAndUserIdAfterUpdate() {
                // Arrange
                UUID userId = UUID.randomUUID();
                UUID orgId = UUID.randomUUID();

                UpdateOrganizationCommand command = new UpdateOrganizationCommand(
                                orgId,
                                "Updated Name");

                OrganizationEntity organization = new OrganizationEntity(userId, "Old Name");
                organization.setId(orgId);

                when(repository.findByIdAndUserID(orgId, userId)).thenReturn(Optional.of(organization));
                when(repository.save(any(OrganizationEntity.class))).thenReturn(organization);

                // Act
                updateService.execute(userId.toString(), command);

                // Assert
                // Verify that the ID and userID were not changed
                verify(repository).save(argThat(org -> org.getId().equals(orgId) && org.getUserID().equals(userId)));
        }
}
