package com.euandresimoes.spring_crm.auth;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.euandresimoes.spring_crm.auth.dto.*;
import com.euandresimoes.spring_crm.auth.exception.*;
import com.euandresimoes.spring_crm.shared.security.JwtService;

// No import needed for same package JwtService
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // --- Create User Tests ---

    @Test
    @DisplayName("Should create a new user successfully when data is valid")
    void shouldCreateUserSuccessfully() {
        CreateUserCommand command = new CreateUserCommand("usuario@exemplo.com", "senha123");
        when(repository.findByEmail(command.email())).thenReturn(Optional.empty());
        String encodedPassword = "$2a$10$encodedPassword";
        when(passwordEncoder.encode(command.password())).thenReturn(encodedPassword);

        UUID userId = UUID.randomUUID();
        UserEntity savedUser = new UserEntity(command.email(), encodedPassword, true, UserRoles.USER);
        savedUser.setId(userId);
        when(repository.save(any(UserEntity.class))).thenReturn(savedUser);

        CreateUserResponse response = authService.createUser(command);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo(command.email());
        assertThat(response.role()).isEqualTo("USER");

        verify(repository).findByEmail(command.email());
        verify(passwordEncoder).encode(command.password());
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException when the email is already registered")
    void shouldThrowEmailAlreadyInUseException() {
        CreateUserCommand command = new CreateUserCommand("usuario.existente@exemplo.com", "senha123");
        UserEntity existingUser = new UserEntity(command.email(), "$2a$10$encodedPassword", true, UserRoles.USER);
        when(repository.findByEmail(command.email())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authService.createUser(command))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessageContaining(command.email());

        verify(repository, never()).save(any(UserEntity.class));
    }

    // --- Login Tests ---

    @Test
    @DisplayName("Should login successfully when credentials are valid")
    void shouldLoginSuccessfully() throws Exception {
        LoginCommand command = new LoginCommand("usuario@exemplo.com", "senha123");
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(command.email(), "$2a$10$encodedPassword", true, UserRoles.USER);
        user.setId(userId);

        when(repository.findByEmail(command.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), user.getPasswordHash())).thenReturn(true);
        String expectedToken = "token";
        when(jwtService.generate(userId.toString(), "USER")).thenReturn(expectedToken);

        String token = authService.login(command);

        assertThat(token).isEqualTo(expectedToken);
        verify(jwtService).generate(userId.toString(), "USER");
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when the password is incorrect")
    void shouldThrowInvalidCredentialsException() {
        LoginCommand command = new LoginCommand("usuario@exemplo.com", "senhaErrada");
        UserEntity user = new UserEntity(command.email(), "$2a$10$encodedPassword", true, UserRoles.USER);

        when(repository.findByEmail(command.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }

    // --- Delete User Tests ---

    @Test
    @DisplayName("Should delete user by ID successfully")
    void shouldDeleteUserByIdSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity("usuario@exemplo.com", "$2a$10$encoded", true, UserRoles.USER);
        user.setId(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        authService.deleteUserByID(userId);

        verify(repository).delete(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting by non-existent ID")
    void shouldThrowExceptionWhenDeletingByNonExistentId() {
        UUID userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.deleteUserByID(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- Find User Tests ---

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        String email = "usuario@exemplo.com";
        UserEntity user = new UserEntity(email, "$2a$10$encoded", true, UserRoles.USER);
        user.setId(UUID.randomUUID());
        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        FindUserResponse response = authService.findUserByEmail(email);

        assertThat(response.email()).isEqualTo(email);
        verify(repository).findByEmail(email);
    }

    @Test
    @DisplayName("Should list users with pagination correctly")
    void shouldListUsersWithPagination() {
        UserEntity user1 = new UserEntity("u1@email.com", "$2a$10$1", true, UserRoles.USER);
        user1.setId(UUID.randomUUID());
        UserEntity user2 = new UserEntity("u2@email.com", "$2a$10$2", true, UserRoles.ADMIN);
        user2.setId(UUID.randomUUID());

        Page<UserEntity> pageResult = new PageImpl<>(Arrays.asList(user1, user2));
        when(repository.findAll(any(Pageable.class))).thenReturn(pageResult);

        List<FindUserResponse> responses = authService.findAllUsers(0, 10);

        assertThat(responses).hasSize(2);
        verify(repository).findAll(PageRequest.of(0, 10));
    }

    // --- Profile Tests ---

    @Test
    @DisplayName("Should return the user profile successfully when the ID is valid")
    void shouldReturnProfileSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity("usuario@exemplo.com", "$2a$10$encoded", true, UserRoles.USER);
        user.setId(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        ProfileResponse response = authService.getProfile(userId);

        assertThat(response.email()).isEqualTo(user.getEmail());
        assertThat(response.id()).isEqualTo(userId.toString());
    }
}
