package com.euandresimoes.spring_crm.auth.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.dto.ProfileResponse;
import com.euandresimoes.spring_crm.auth.domain.exception.UserNotFoundException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;

@Service
public class ProfileService {

    private final UserRepository repo;

    public ProfileService(UserRepository repo) {
        this.repo = repo;
    }

    public ProfileResponse execute(String id) {
        UserEntity user = repo.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException(id));

        return new ProfileResponse(
                id,
                user.getEmail(),
                user.getRole());
    }

}
