package com.euandresimoes.spring_crm.auth.domain.dto;

public record CreateUserCommand(
        String email,
        String password
) {
}
