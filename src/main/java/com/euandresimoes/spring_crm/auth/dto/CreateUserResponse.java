package com.euandresimoes.spring_crm.auth.dto;

import java.util.UUID;

public record CreateUserResponse(UUID id, String email, String role) {
}
