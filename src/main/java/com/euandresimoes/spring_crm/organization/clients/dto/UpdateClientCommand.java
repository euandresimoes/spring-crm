package com.euandresimoes.spring_crm.organization.clients.dto;

import com.euandresimoes.spring_crm.organization.clients.ClientStatus;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateClientCommand(
        @NotNull(message = "Client id is required") UUID id,
        @NotBlank(message = "Client name is required") @Length(min = 5, max = 30, message = "Client name must be between 5 and 30 characters") String name,
        String description,
        @NotBlank(message = "Client email is required") @Email(message = "Client email is invalid") String email,
        @Length(min = 11, max = 14, message = "Client cpf_cnpj must be between 11 and 14 characters") String cpf_cnpj,
        @Length(min = 5, max = 20, message = "Client phone must be between 5 and 20 characters") String phone,
        @NotNull(message = "Client status is required") ClientStatus status) {

}
