package com.euandresimoes.spring_crm.auth.application;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.UserRoles;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the user deletion service.
 * Validates deletion operations by ID and by email.
 */
@ExtendWith(MockitoExtension.class)
class DeleteServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private DeleteUserService deleteService;

    @Test
    @DisplayName("Should delete user by ID successfully")
    void shouldDeleteUserByIdSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        UserEntity user = new UserEntity(
                "usuario@exemplo.com",
                "$2a$10$encodedPassword",
                true,
                UserRoles.USER);
        user.setId(userId);

        // Simulate that the user was found
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        deleteService.deleteByID(userIdString);

        // Assert
        // Verify that the repository was consulted
        verify(repository).findById(userId);
        // Verify that the delete method was called with the correct user
        verify(repository).delete(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting by a non-existent ID")
    void shouldThrowExceptionWhenDeletingByNonExistentId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        // Simulate that the user was not found
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteService.deleteByID(userIdString))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userIdString);

        // Verify that the repository was consulted
        verify(repository).findById(userId);
        // Verify that delete was never called
        verify(repository, never()).delete(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should delete user by email successfully")
    void shouldDeleteUserByEmailSuccessfully() {
        // Arrange
        String email = "usuario@exemplo.com";

        UserEntity user = new UserEntity(
                email,
                "$2a$10$encodedPassword",
                true,
                UserRoles.USER);

        // Simulate that the user was found
        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        deleteService.deleteByEmail(email);

        // Assert
        // Verify that the repository was consulted
        verify(repository).findByEmail(email);
        // Verify that the delete method was called with the correct user
        verify(repository).delete(user);
    }

    @Test
    @DisplayName("Should throw EmailNotFoundException when deleting by a non-existent email")
    void shouldThrowExceptionWhenDeletingByNonExistentEmail() {
        // Arrange
        String email = "naoexiste@exemplo.com";

        // Simulate that the email was not found
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteService.deleteByEmail(email))
                .isInstanceOf(EmailNotFoundException.class)
                .hasMessageContaining(email);

        // Verify that the repository was consulted
        verify(repository).findByEmail(email);
        // Verify that delete was never called
        verify(repository, never()).delete(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should correctly convert the ID from String to UUID when deleting")
    void shouldCorrectlyConvertIdWhenDeleting() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        UserEntity user = new UserEntity(
                "usuario@exemplo.com",
                "$2a$10$encodedPassword",
                true,
                UserRoles.USER);
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        deleteService.deleteByID(userIdString);

        // Assert
        // Verify that the repository was called with the correct UUID
        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("Should delete ADMIN user by ID")
    void shouldDeleteAdminUserById() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        UserEntity userAdmin = new UserEntity(
                "admin@exemplo.com",
                "$2a$10$encodedPassword",
                true,
                UserRoles.ADMIN);
        userAdmin.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(userAdmin));

        // Act
        deleteService.deleteByID(userIdString);

        // Assert
        // Verify that the ADMIN user was deleted normally
        verify(repository).delete(userAdmin);
    }

    @Test
    @DisplayName("Should delete inactive user by email")
    void shouldDeleteInactiveUserByEmail() {
        // Arrange
        String email = "inativo@exemplo.com";

        UserEntity userInactive = new UserEntity(
                email,
                "$2a$10$encodedPassword",
                false, // inactive user
                UserRoles.USER);

        when(repository.findByEmail(email)).thenReturn(Optional.of(userInactive));

        // Act
        deleteService.deleteByEmail(email);

        // Assert
        // Verify that the inactive user was deleted normally
        verify(repository).delete(userInactive);
    }
}
