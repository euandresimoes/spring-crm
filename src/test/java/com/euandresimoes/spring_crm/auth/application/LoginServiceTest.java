package com.euandresimoes.spring_crm.auth.application;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.UserRoles;
import com.euandresimoes.spring_crm.auth.domain.dto.LoginCommand;
import com.euandresimoes.spring_crm.auth.domain.exception.AccountNotActiveException;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.domain.exception.InvalidCredentialsException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;
import com.euandresimoes.spring_crm.auth.infra.security.jwt.JwtService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the authentication service (login).
 * Validates success and failure scenarios in the login process.
 */
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

        @Mock
        private UserRepository repository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @InjectMocks
        private LoginService loginService;

        @Test
        @DisplayName("Should login successfully when credentials are valid")
        void shouldLoginSuccessfully() throws Exception {
                // Arrange
                // Create the login command
                LoginCommand command = new LoginCommand(
                                "usuario@exemplo.com",
                                "senha123");

                // Create an active user in the database
                UUID userId = UUID.randomUUID();
                UserEntity usuario = new UserEntity(
                                command.email(),
                                "$2a$10$encodedPassword",
                                true, // active user
                                UserRoles.USER);
                usuario.setId(userId);

                // Simulate that the user was found
                when(repository.findByEmail(command.email())).thenReturn(Optional.of(usuario));

                // Simulate that the password is correct
                when(passwordEncoder.matches(command.password(), usuario.getPasswordHash())).thenReturn(true);

                // Simulate the JWT token generation
                String tokenEsperado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
                when(jwtService.generate(userId.toString(), "USER")).thenReturn(tokenEsperado);

                // Act
                String token = loginService.execute(command);

                // Assert
                // Verify that the token was returned
                assertThat(token).isNotNull();
                assertThat(token).isEqualTo(tokenEsperado);

                // Verify that the methods were called correctly
                verify(repository).findByEmail(command.email());
                verify(passwordEncoder).matches(command.password(), usuario.getPasswordHash());
                verify(jwtService).generate(userId.toString(), "USER");
        }

        @Test
        @DisplayName("Should throw EmailNotFoundException when the email does not exist")
        void shouldThrowEmailNotFoundException() {
                // Arrange
                LoginCommand command = new LoginCommand(
                                "naoexiste@exemplo.com",
                                "senha123");

                // Simulate that the email was not found
                when(repository.findByEmail(command.email())).thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> loginService.execute(command))
                                .isInstanceOf(EmailNotFoundException.class)
                                .hasMessageContaining(command.email());

                // Verify that the repository was consulted
                verify(repository).findByEmail(command.email());
                // Verify that the password was never checked
                verify(passwordEncoder, never()).matches(anyString(), anyString());
                // Verify that the token was never generated
                verify(jwtService, never()).generate(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw AccountNotActiveException when the account is inactive")
        void shouldThrowAccountNotActiveException() {
                // Arrange
                LoginCommand command = new LoginCommand(
                                "usuario.inativo@exemplo.com",
                                "senha123");

                // Create an INACTIVE user
                UserEntity usuarioInativo = new UserEntity(
                                command.email(),
                                "$2a$10$encodedPassword",
                                false, // inactive user
                                UserRoles.USER);

                when(repository.findByEmail(command.email())).thenReturn(Optional.of(usuarioInativo));

                // Act & Assert
                assertThatThrownBy(() -> loginService.execute(command))
                                .isInstanceOf(AccountNotActiveException.class)
                                .hasMessageContaining(command.email());

                // Verify that the repository was consulted
                verify(repository).findByEmail(command.email());
                // Verify that the password was never checked
                verify(passwordEncoder, never()).matches(anyString(), anyString());
                // Verify that the token was never generated
                verify(jwtService, never()).generate(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when the password is incorrect")
        void shouldThrowInvalidCredentialsException() {
                // Arrange
                LoginCommand command = new LoginCommand(
                                "usuario@exemplo.com",
                                "senhaErrada");

                // Create an active user
                UserEntity usuario = new UserEntity(
                                command.email(),
                                "$2a$10$encodedPassword",
                                true,
                                UserRoles.USER);

                when(repository.findByEmail(command.email())).thenReturn(Optional.of(usuario));

                // Simulate that the password DOES NOT match
                when(passwordEncoder.matches(command.password(), usuario.getPasswordHash())).thenReturn(false);

                // Act & Assert
                assertThatThrownBy(() -> loginService.execute(command))
                                .isInstanceOf(InvalidCredentialsException.class)
                                .hasMessageContaining("Invalid credentials");

                // Verify that the repository was consulted
                verify(repository).findByEmail(command.email());
                // Verify that the password was checked
                verify(passwordEncoder).matches(command.password(), usuario.getPasswordHash());
                // Verify that the token was never generated
                verify(jwtService, never()).generate(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw Exception when an error occurs during JWT token generation")
        void shouldThrowExceptionWhenTokenGenerationFails() {
                // Arrange
                LoginCommand command = new LoginCommand(
                                "usuario@exemplo.com",
                                "senha123");

                UUID userId = UUID.randomUUID();
                UserEntity usuario = new UserEntity(
                                command.email(),
                                "$2a$10$encodedPassword",
                                true,
                                UserRoles.USER);
                usuario.setId(userId);

                when(repository.findByEmail(command.email())).thenReturn(Optional.of(usuario));
                when(passwordEncoder.matches(command.password(), usuario.getPasswordHash())).thenReturn(true);

                // Simulate an error during token generation
                when(jwtService.generate(userId.toString(), "USER"))
                                .thenThrow(new JWTCreationException("Erro ao criar token", null));

                // Act & Assert
                assertThatThrownBy(() -> loginService.execute(command))
                                .isInstanceOf(Exception.class)
                                .hasMessageContaining("Erro ao criar token");

                // Verify that all previous steps were executed
                verify(repository).findByEmail(command.email());
                verify(passwordEncoder).matches(command.password(), usuario.getPasswordHash());
                verify(jwtService).generate(userId.toString(), "USER");
        }

        @Test
        @DisplayName("Should generate token with the correct user ID and role")
        void shouldGenerateTokenWithCorrectData() throws Exception {
                // Arrange
                LoginCommand command = new LoginCommand(
                                "admin@exemplo.com",
                                "senha123");

                // Create an ADMIN user
                UUID userId = UUID.randomUUID();
                UserEntity usuarioAdmin = new UserEntity(
                                command.email(),
                                "$2a$10$encodedPassword",
                                true,
                                UserRoles.ADMIN);
                usuarioAdmin.setId(userId);

                when(repository.findByEmail(command.email())).thenReturn(Optional.of(usuarioAdmin));
                when(passwordEncoder.matches(command.password(), usuarioAdmin.getPasswordHash())).thenReturn(true);
                when(jwtService.generate(userId.toString(), "ADMIN")).thenReturn("token");

                // Act
                loginService.execute(command);

                // Assert
                // Verify that the token was generated with the correct ID and role
                verify(jwtService).generate(userId.toString(), "ADMIN");
        }
}
