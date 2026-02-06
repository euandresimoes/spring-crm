package com.euandresimoes.spring_crm.auth.application;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.UserRoles;
import com.euandresimoes.spring_crm.auth.domain.dto.ProfileResponse;
import com.euandresimoes.spring_crm.auth.domain.exception.UserNotFoundException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the user profile service.
 * Validates obtaining profile information for the authenticated user.
 */
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    @DisplayName("Should return the user profile successfully when the ID is valid")
    void shouldReturnProfileSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        // Create an existing user
        UserEntity usuario = new UserEntity(
                "usuario@exemplo.com",
                "$2a$10$encodedPassword",
                true,
                UserRoles.USER);
        usuario.setId(userId);

        // Simulate that the user was found
        when(repository.findById(userId)).thenReturn(Optional.of(usuario));

        // Act
        ProfileResponse response = profileService.execute(userIdString);

        // Assert
        // Verify that the response contains the correct data
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userIdString);
        assertThat(response.email()).isEqualTo(usuario.getEmail());
        assertThat(response.role()).isEqualTo(usuario.getRole());

        // Verify that the repository was consulted
        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when the user does not exist")
    void shouldThrowUserNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        // Simulate that the user was not found
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profileService.execute(userIdString))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userIdString);

        // Verify that the repository was consulted
        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("Should return profile with ADMIN role when the user is an administrator")
    void shouldReturnAdminProfile() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        // Create an ADMIN user
        UserEntity usuarioAdmin = new UserEntity(
                "admin@exemplo.com",
                "$2a$10$encodedPassword",
                true,
                UserRoles.ADMIN);
        usuarioAdmin.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(usuarioAdmin));

        // Act
        ProfileResponse response = profileService.execute(userIdString);

        // Assert
        // Verify that the returned profile has the ADMIN role
        assertThat(response.role()).isEqualTo("ADMIN");
        assertThat(response.email()).isEqualTo("admin@exemplo.com");
    }

    @Test
    @DisplayName("Should correctly convert the ID from String to UUID")
    void shouldCorrectlyConvertId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        UserEntity usuario = new UserEntity(
                "usuario@exemplo.com",
                "$2a$10$encodedPassword",
                true,
                UserRoles.USER);
        usuario.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(usuario));

        // Act
        profileService.execute(userIdString);

        // Assert
        // Verify that the repository was called with the correct UUID
        verify(repository).findById(userId);
    }
}
