package com.euandresimoes.spring_crm.auth.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import com.euandresimoes.spring_crm.auth.domain.dto.FindUserResponse;
import com.euandresimoes.spring_crm.auth.domain.exception.EmailNotFoundException;
import com.euandresimoes.spring_crm.auth.domain.exception.UserNotFoundException;
import com.euandresimoes.spring_crm.auth.infra.repository.UserRepository;

@Service
public class FindService {

    private final UserRepository repo;

    public FindService(UserRepository repo) {
        this.repo = repo;
    }

    public FindUserResponse findByID(String id) {
        UserEntity user = repo.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    throw new UserNotFoundException(id);
                });

        return new FindUserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRole());
    }

    public FindUserResponse findByEmail(String email) {
        UserEntity user = repo.findByEmail(email)
                .orElseThrow(() -> {
                    throw new EmailNotFoundException(email);
                });

        return new FindUserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRole());
    }

    public List<FindUserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> pageResult = repo.findAll(pageable);

        List<FindUserResponse> list = pageResult.stream()
                .map(u -> new FindUserResponse(u.getId().toString(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());

        return list;
    }
}
