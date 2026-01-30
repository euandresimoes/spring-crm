package com.euandresimoes.spring_crm.auth.application;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.UserRoles;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserCommand;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserResponse;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailAlreadyInUseException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService {
    private final UserRepository repo;
    private final PasswordEncoder pwdEncoder;

    public CreateUserService(UserRepository repo, PasswordEncoder pwdEncoder) {
        this.repo = repo;
        this.pwdEncoder = pwdEncoder;
    }

    public CreateUserResponse execute(CreateUserCommand command) {
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
}