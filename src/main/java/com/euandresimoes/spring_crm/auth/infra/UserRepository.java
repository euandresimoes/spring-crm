package com.euandresimoes.spring_crm.auth.infra;

import com.euandresimoes.spring_crm.auth.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

}
