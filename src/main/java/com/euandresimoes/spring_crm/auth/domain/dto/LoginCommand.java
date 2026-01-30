package com.euandresimoes.spring_crm.auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginCommand(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 50, message = "Email must be less than 50 characters") String email,

        @NotBlank(message = "Password is required") @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters") @Pattern(regexp = "^[A-Za-z\\d@$!%*?&]+$", message = "Password must contain only letters and numbers") String password) {
}
