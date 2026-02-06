package com.euandresimoes.spring_crm.auth.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.domain.exception.UserNotFoundException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DeleteUserService {

    private final UserRepository repo;

    public DeleteUserService(UserRepository repo) {
        this.repo = repo;
    }

    public void deleteByID(String id) {
        repo.findById(UUID.fromString(id))
                .ifPresentOrElse(
                        u -> repo.delete(u),
                        () -> {
                            throw new UserNotFoundException(id);
                        });
    }

    public void deleteByEmail(String email) {
        repo.findByEmail(email)
                .ifPresentOrElse(
                        u -> repo.delete(u),
                        () -> {
                            throw new EmailNotFoundException(email);
                        });
    }

}
