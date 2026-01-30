package com.euandresimoes.spring_crm.auth.domain.dto;

import java.util.UUID;

public record CreateUserResponse(UUID id, String email) {}
