package com.euandresimoes.spring_crm.auth.application;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.UserRoles;
import com.euandresimoes.spring_crm.auth.domain.dto.FindUserResponse;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.domain.exception.UserNotFoundException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the user search service.
 * Validates search operations by ID, email, and paginated listing.
 */
@ExtendWith(MockitoExtension.class)
class FindServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private FindUserService findService;

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserByIdSuccessfully() {
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
        FindUserResponse response = findService.findByID(userIdString);

        // Assert
        // Verify that the response contains the correct data
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userIdString);
        assertThat(response.email()).isEqualTo(user.getEmail());
        assertThat(response.role()).isEqualTo(user.getRole());

        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when searching for a non-existent ID")
    void shouldThrowExceptionWhenSearchingForNonExistentId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findService.findByID(userIdString))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userIdString);

        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        // Arrange
        String email = "usuario@exemplo.com";
        UUID userId = UUID.randomUUID();

        UserEntity user = new UserEntity(
                email,
                "$2a$10$encodedPassword",
                true,
                UserRoles.USER);
        user.setId(userId);

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        FindUserResponse response = findService.findByEmail(email);

        // Assert
        // Verify that the response contains the correct data
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userId.toString());
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.role()).isEqualTo("USER");

        verify(repository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw EmailNotFoundException when searching for a non-existent email")
    void shouldThrowExceptionWhenSearchingForNonExistentEmail() {
        // Arrange
        String email = "naoexiste@exemplo.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findService.findByEmail(email))
                .isInstanceOf(EmailNotFoundException.class)
                .hasMessageContaining(email);

        verify(repository).findByEmail(email);
    }

    @Test
    @DisplayName("Should list users with pagination correctly")
    void shouldListUsersWithPagination() {
        // Arrange
        int page = 0;
        int size = 10;

        // Create a list of users
        UUID userId1 = UUID.randomUUID();
        UserEntity user1 = new UserEntity(
                "usuario1@exemplo.com",
                "$2a$10$encoded1",
                true,
                UserRoles.USER);
        user1.setId(userId1);

        UUID userId2 = UUID.randomUUID();
        UserEntity user2 = new UserEntity(
                "usuario2@exemplo.com",
                "$2a$10$encoded2",
                true,
                UserRoles.ADMIN);
        user2.setId(userId2);

        List<UserEntity> users = Arrays.asList(user1, user2);
        Page<UserEntity> pageResult = new PageImpl<>(users);

        when(repository.findAll(any(Pageable.class))).thenReturn(pageResult);

        // Act
        List<FindUserResponse> responses = findService.findAll(page, size);

        // Assert
        // Verify that the response contains the correct data
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);

        // Verify the first user
        assertThat(responses.get(0).id()).isEqualTo(userId1.toString());
        assertThat(responses.get(0).email()).isEqualTo("usuario1@exemplo.com");
        assertThat(responses.get(0).role()).isEqualTo("USER");

        // Verify the second user
        assertThat(responses.get(1).id()).isEqualTo(userId2.toString());
        assertThat(responses.get(1).email()).isEqualTo("usuario2@exemplo.com");
        assertThat(responses.get(1).role()).isEqualTo("ADMIN");

        // Verify that the repository was called with correct pagination
        verify(repository).findAll(PageRequest.of(page, size));
    }

    @Test
    @DisplayName("Should return empty list when there are no users")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Arrange
        int page = 0;
        int size = 10;

        Page<UserEntity> emptyPage = new PageImpl<>(Arrays.asList());

        when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<FindUserResponse> responses = findService.findAll(page, size);

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(repository).findAll(PageRequest.of(page, size));
    }

    @Test
    @DisplayName("Should respect pagination parameters")
    void shouldRespectPaginationParameters() {
        // Arrange
        int page = 2;
        int size = 5;

        Page<UserEntity> pageResult = new PageImpl<>(Arrays.asList());
        when(repository.findAll(any(Pageable.class))).thenReturn(pageResult);

        // Act
        findService.findAll(page, size);

        // Assert
        // Verify that the PageRequest was created with the correct parameters
        verify(repository).findAll(PageRequest.of(page, size));
    }

    @Test
    @DisplayName("Should correctly map entities to responses in the listing")
    void shouldCorrectlyMapEntitiesToResponses() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(
                "teste@exemplo.com",
                "$2a$10$encoded",
                true,
                UserRoles.ADMIN);
        user.setId(userId);

        Page<UserEntity> pageResult = new PageImpl<>(Arrays.asList(user));
        when(repository.findAll(any(Pageable.class))).thenReturn(pageResult);

        // Act
        List<FindUserResponse> responses = findService.findAll(0, 10);

        // Assert
        // Verify that the response contains the correct data
        assertThat(responses).hasSize(1);
        FindUserResponse response = responses.get(0);

        // Verify that all fields were mapped correctly
        assertThat(response.id()).isEqualTo(userId.toString());
        assertThat(response.email()).isEqualTo("teste@exemplo.com");
        assertThat(response.role()).isEqualTo("ADMIN");
    }
}
