package com.euandresimoes.spring_crm.organization.organization_core.application;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.domain.dto.OrganizationResponse;
import com.euandresimoes.spring_crm.organization.organization_core.infra.repository.OrganizationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the search organization service.
 * Validates listing organizations by user.
 */
@ExtendWith(MockitoExtension.class)
class FindServiceTest {

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private FindOrganizationService findService;

    @Test
    @DisplayName("Should list all user's organizations successfully")
    void shouldListUserOrganizationsSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        // Create user's organizations
        UUID orgId1 = UUID.randomUUID();
        OrganizationEntity org1 = new OrganizationEntity(userId, "Company A");
        org1.setId(orgId1);

        UUID orgId2 = UUID.randomUUID();
        OrganizationEntity org2 = new OrganizationEntity(userId, "Company B");
        org2.setId(orgId2);

        List<OrganizationEntity> organizations = Arrays.asList(org1, org2);

        when(repository.findAllByUserID(userId)).thenReturn(organizations);

        // Act
        List<OrganizationResponse> responses = findService.findAll(userIdString);

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);

        // Verify the first organization
        assertThat(responses.get(0).id()).isEqualTo(orgId1);
        assertThat(responses.get(0).name()).isEqualTo("Company A");

        // Verify the second organization
        assertThat(responses.get(1).id()).isEqualTo(orgId2);
        assertThat(responses.get(1).name()).isEqualTo("Company B");

        verify(repository).findAllByUserID(userId);
    }

    @Test
    @DisplayName("Should return an empty list when the user has no organizations")
    void shouldReturnEmptyListWhenUserHasNoOrganizations() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        // Simulate that the user has no organizations
        when(repository.findAllByUserID(userId)).thenReturn(Arrays.asList());

        // Act
        List<OrganizationResponse> responses = findService.findAll(userIdString);

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(repository).findAllByUserID(userId);
    }

    @Test
    @DisplayName("Should correctly convert the userID from String to UUID")
    void shouldCorrectlyConvertUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        when(repository.findAllByUserID(userId)).thenReturn(Arrays.asList());

        // Act
        findService.findAll(userIdString);

        // Assert
        // Verify that the UUID was converted correctly
        verify(repository).findAllByUserID(userId);
    }

    @Test
    @DisplayName("Should correctly map entities to responses")
    void shouldCorrectlyMapEntitiesToResponses() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        String organizationName = "Tech Solutions LTDA";

        OrganizationEntity organization = new OrganizationEntity(userId, organizationName);
        organization.setId(orgId);

        when(repository.findAllByUserID(userId)).thenReturn(Arrays.asList(organization));

        // Act
        List<OrganizationResponse> responses = findService.findAll(userId.toString());

        // Assert (Verificar)
        assertThat(responses).hasSize(1);
        OrganizationResponse response = responses.get(0);

        // Verifica que todos String organizationName = "Tech Solutions LTDA";
        // Verify that all fields were correctly mapped
        assertThat(response.id()).isEqualTo(orgId);
        assertThat(response.name()).isEqualTo(organizationName);
    }

    @Test
    @DisplayName("Should return only organizations for the specified user")
    void shouldReturnOnlyOrganizationsForSpecifiedUser() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        // Organizations for user 1
        OrganizationEntity org1User1 = new OrganizationEntity(userId1, "Org 1 User 1");
        org1User1.setId(UUID.randomUUID());

        OrganizationEntity org2User1 = new OrganizationEntity(userId1, "Org 2 User 1");
        org2User1.setId(UUID.randomUUID());

        List<OrganizationEntity> orgsUser1 = Arrays.asList(org1User1, org2User1);

        // Simulate that the repository returns only organizations for userId1
        when(repository.findAllByUserID(userId1)).thenReturn(orgsUser1);

        // Act
        List<OrganizationResponse> responses = findService.findAll(userId1.toString());

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Org 1 User 1");
        assertThat(responses.get(1).name()).isEqualTo("Org 2 User 1");

        // Verify that only userId1 was consulted
        verify(repository).findAllByUserID(userId1);
    }

    @Test
    @DisplayName("Should list organizations with names containing special characters")
    void shouldListOrganizationsWithSpecialNames() {
        // Arrange
        UUID userId = UUID.randomUUID();

        OrganizationEntity org1 = new OrganizationEntity(
                userId,
                "Empresa & Cia. LTDA");
        org1.setId(UUID.randomUUID());

        OrganizationEntity org2 = new OrganizationEntity(
                userId,
                "Tech Solutions (2024) - Inovação");
        org2.setId(UUID.randomUUID());

        when(repository.findAllByUserID(userId)).thenReturn(Arrays.asList(org1, org2));

        // Act
        List<OrganizationResponse> responses = findService.findAll(userId.toString());

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Empresa & Cia. LTDA");
        assertThat(responses.get(1).name()).isEqualTo("Tech Solutions (2024) - Inovação");
    }
}
