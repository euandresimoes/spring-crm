package com.euandresimoes.spring_crm.auth.dto;

public record FindUserResponse(
        String id,
        String email,
        String role) {

    public FindUserResponse(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
