package com.euandresimoes.spring_crm.organization.organization_core.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import java.util.UUID;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for the organization deletion service.
 * Validates organization deletion with access control by user.
 */
@ExtendWith(MockitoExtension.class)
class DeleteServiceTest {

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private DeleteOrganizationService deleteService;

    @Test
    @DisplayName("Should delete an organization successfully")
    void shouldDeleteOrganizationSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();
        UUID orgId = UUID.randomUUID();

        // Act
        deleteService.execute(userIdString, orgId);

        // Assert
        // Verify that the repository was called with the correct parameters
        verify(repository).deleteByIdAndUserID(orgId, userId);
    }

    @Test
    @DisplayName("Should correctly convert the userID from String to UUID")
    void shouldCorrectlyConvertUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();
        UUID orgId = UUID.randomUUID();

        // Act
        deleteService.execute(userIdString, orgId);

        // Assert
        // Verify that the UUID was converted correctly
        verify(repository).deleteByIdAndUserID(orgId, userId);
    }

    @Test
    @DisplayName("Should ensure that only the user's organizations are deleted")
    void shouldEnsureOnlyUserOrganizationsAreDeleted() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        // Act
        // Attempt to delete with userId1
        deleteService.execute(userId1.toString(), orgId);

        // Assert
        // Verify that it was called with userId1, not userId2
        verify(repository).deleteByIdAndUserID(orgId, userId1);
    }

    @Test
    @DisplayName("Should accept different organization UUIDs")
    void shouldAcceptDifferentOrganizationUuids() {
        // Arrange pockets
        UUID userId = UUID.randomUUID();
        UUID orgId1 = UUID.randomUUID();
        UUID orgId2 = UUID.randomUUID();

        // Act
        deleteService.execute(userId.toString(), orgId1);
        deleteService.execute(userId.toString(), orgId2);

        // Assert
        // Verify that both deletions were called correctly
        verify(repository).deleteByIdAndUserID(orgId1, userId);
        verify(repository).deleteByIdAndUserID(orgId2, userId);
    }

    @Test
    @DisplayName("Should pass the parameters in the correct order to the repository")
    void shouldPassParametersInCorrectOrder() {
        // Arrange pocket
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        // Act
        deleteService.execute(userId.toString(), orgId);

        // Assert
        // Verify that the parameters were passed in the correct order:
        // first orgId, then userId
        verify(repository).deleteByIdAndUserID(orgId, userId);
    }
}
