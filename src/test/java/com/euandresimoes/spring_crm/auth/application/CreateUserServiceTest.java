package com.euandresimoes.spring_crm.auth.application;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.UserRoles;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserCommand;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserResponse;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailAlreadyInUseException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the user creation service.
 * Validates success and failure scenarios for creating new users.
 */
@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

        @Mock
        private UserRepository repository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private CreateUserService createUserService;

        @Test
        @DisplayName("Should create a new user successfully when data is valid")
        void shouldCreateUserSuccessfully() {
                // Arrange
                // Create the command with the new user data
                CreateUserCommand command = new CreateUserCommand(
                                "usuario@exemplo.com",
                                "senha123");

                // Simulate that the email does not exist in the database yet
                when(repository.findByEmail(command.email())).thenReturn(Optional.empty());

                // Simulate the encoded password
                String senhaEncodada = "$2a$10$encodedPassword";
                when(passwordEncoder.encode(command.password())).thenReturn(senhaEncodada);

                // Create the entity that will be returned after the save
                UUID userId = UUID.randomUUID();
                UserEntity usuarioSalvo = new UserEntity(
                                command.email(),
                                senhaEncodada,
                                true,
                                UserRoles.USER);
                // Set the ID manually (simulating what the database would do)
                usuarioSalvo.setId(userId);

                when(repository.save(any(UserEntity.class))).thenReturn(usuarioSalvo);

                // Act
                // Execute the method being tested
                CreateUserResponse response = createUserService.execute(command);

                // Assert
                // Verify that the response contains the correct data
                assertThat(response).isNotNull();
                assertThat(response.id()).isEqualTo(userId);
                assertThat(response.email()).isEqualTo(command.email());
                assertThat(response.role()).isEqualTo("USER");

                // Verify that the methods were called correctly
                verify(repository).findByEmail(command.email());
                verify(passwordEncoder).encode(command.password());
                verify(repository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should throw EmailAlreadyInUseException when the email is already registered")
        void shouldThrowEmailAlreadyInUseException() {
                // Arrange
                // Create the command with an existing email
                CreateUserCommand command = new CreateUserCommand(
                                "usuario.existente@exemplo.com",
                                "senha123");

                // Simulate that a user with this email already exists
                UserEntity usuarioExistente = new UserEntity(
                                command.email(),
                                "$2a$10$encodedPassword",
                                true,
                                UserRoles.USER);
                when(repository.findByEmail(command.email())).thenReturn(Optional.of(usuarioExistente));

                // Act & Assert
                // Verify that the correct exception is thrown
                assertThatThrownBy(() -> createUserService.execute(command))
                                .isInstanceOf(EmailAlreadyInUseException.class)
                                .hasMessageContaining(command.email());

                // Verify that the repository was consulted
                verify(repository).findByEmail(command.email());
                // Verify that save was never called
                verify(repository, never()).save(any(UserEntity.class));
                // Verify that the password was never encoded
                verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("Should encode the password before saving the user")
        void shouldEncodePasswordBeforeSaving() {
                // Arrange
                String senhaOriginal = "minhaSenhaSegura123";
                CreateUserCommand command = new CreateUserCommand(
                                "novo@exemplo.com",
                                senhaOriginal);

                when(repository.findByEmail(command.email())).thenReturn(Optional.empty());

                String senhaEncodada = "$2a$10$hashedPassword";
                when(passwordEncoder.encode(senhaOriginal)).thenReturn(senhaEncodada);

                UserEntity usuarioSalvo = new UserEntity(
                                command.email(),
                                senhaEncodada,
                                true,
                                UserRoles.USER);
                when(repository.save(any(UserEntity.class))).thenReturn(usuarioSalvo);

                // Act
                createUserService.execute(command);

                // Assert
                // Verify that the encoder was called with the original password
                verify(passwordEncoder).encode(senhaOriginal);

                // Verify that the saved user has the encoded password
                verify(repository).save(argThat(user -> user.getPasswordHash().equals(senhaEncodada)));
        }

        @Test
        @DisplayName("Should create user with USER role by default")
        void shouldCreateUserWithUserRoleByDefault() {
                // Arrange
                CreateUserCommand command = new CreateUserCommand(
                                "usuario@exemplo.com",
                                "senha123");

                when(repository.findByEmail(command.email())).thenReturn(Optional.empty());
                when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");

                UserEntity usuarioSalvo = new UserEntity(
                                command.email(),
                                "$2a$10$encoded",
                                true,
                                UserRoles.USER);
                usuarioSalvo.setId(UUID.randomUUID());
                when(repository.save(any(UserEntity.class))).thenReturn(usuarioSalvo);

                // Act
                CreateUserResponse response = createUserService.execute(command);

                // Assert
                // Verify that the user was created with the USER role
                assertThat(response.role()).isEqualTo("USER");

                // Verify that the saved user has the USER role
                verify(repository).save(argThat(user -> user.getRole().equals("USER")));
        }

        @Test
        @DisplayName("Should create user with active status by default")
        void shouldCreateActiveUserSuccessfully() {
                // Arrange
                CreateUserCommand command = new CreateUserCommand(
                                "usuario@exemplo.com",
                                "senha123");

                when(repository.findByEmail(command.email())).thenReturn(Optional.empty());
                when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");

                UserEntity usuarioSalvo = new UserEntity(
                                command.email(),
                                "$2a$10$encoded",
                                true,
                                UserRoles.USER);
                when(repository.save(any(UserEntity.class))).thenReturn(usuarioSalvo);

                // Act
                createUserService.execute(command);

                // Assert
                // Verify that the user was saved with active status
                verify(repository).save(argThat(user -> user.isActive()));
        }
}
