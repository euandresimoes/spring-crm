package com.euandresimoes.spring_crm.auth.application;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.dto.LoginCommand;
import com.euandresimoes.spring_crm.auth.domain.exception.AccountNotActiveException;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.domain.exception.InvalidCredentialsException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;
import com.euandresimoes.spring_crm.auth.infra.security.jwt.JwtService;
@Service
public class LoginService {

    private final UserRepository repo;
    private final PasswordEncoder pwdEncoder;
    private final JwtService jwtService;

    public LoginService(UserRepository repo, PasswordEncoder pwdEncoder, JwtService jwtService) {
        this.repo = repo;
        this.pwdEncoder = pwdEncoder;
        this.jwtService = jwtService;
    }

    public String execute(LoginCommand command) throws Exception {
        Optional<UserEntity> user = repo.findByEmail(command.email());

        if (user.isEmpty())
            throw new EmailNotFoundException(command.email());

        if (!user.get().isActive())
            throw new AccountNotActiveException(command.email());
        if (!pwdEncoder.matches(command.password(), user.get().getPasswordHash()))
            throw new InvalidCredentialsException("Invalid credentials");

        try {
            var token = jwtService.generate(user.get().getId().toString(), user.get().getRole());
            return token;
        } catch (JWTCreationException e) {
            throw new Exception(e.getMessage());
        }
    }
}