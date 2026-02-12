package com.euandresimoes.spring_crm.auth;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.euandresimoes.spring_crm.auth.dto.*;
import com.euandresimoes.spring_crm.auth.exception.*;
import com.euandresimoes.spring_crm.shared.security.JwtService;

// No import needed for same package JwtService
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder pwdEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repo, PasswordEncoder pwdEncoder, JwtService jwtService) {
        this.repo = repo;
        this.pwdEncoder = pwdEncoder;
        this.jwtService = jwtService;
    }

    public CreateUserResponse createUser(CreateUserCommand command) {
        repo.findByEmail(command.email())
                .ifPresent(user -> {
                    throw new EmailAlreadyInUseException(user.getEmail());
                });

        UserEntity user = repo.save(new UserEntity(
                command.email(),
                pwdEncoder.encode(command.password()),
                true,
                UserRoles.USER));

        return new CreateUserResponse(user.getId(), user.getEmail(), user.getRole());
    }

    public String login(LoginCommand command) throws Exception {
        UserEntity user = repo.findByEmail(command.email())
                .orElseThrow(() -> new EmailNotFoundException(command.email()));

        if (!user.isActive())
            throw new AccountNotActiveException(command.email());
        if (!pwdEncoder.matches(command.password(), user.getPasswordHash()))
            throw new InvalidCredentialsException("Invalid credentials");

        try {
            return jwtService.generate(user.getId().toString(), user.getRole());
        } catch (JWTCreationException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteUserByID(@NonNull UUID id) {
        repo.findById(id)
                .ifPresentOrElse(
                        u -> repo.delete(u),
                        () -> {
                            throw new UserNotFoundException(id.toString());
                        });
    }

    public void deleteUserByEmail(String email) {
        repo.findByEmail(email)
                .ifPresentOrElse(
                        u -> repo.delete(u),
                        () -> {
                            throw new EmailNotFoundException(email);
                        });
    }

    public FindUserResponse findUserByID(@NonNull UUID id) {
        UserEntity user = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        return new FindUserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRole());
    }

    public FindUserResponse findUserByEmail(String email) {
        UserEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        return new FindUserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRole());
    }

    public List<FindUserResponse> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> pageResult = repo.findAll(pageable);

        return pageResult.stream()
                .map(u -> new FindUserResponse(u.getId().toString(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());
    }

    public ProfileResponse getProfile(@NonNull UUID id) {
        UserEntity user = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        return new ProfileResponse(
                id.toString(),
                user.getEmail(),
                user.getRole());
    }
}
