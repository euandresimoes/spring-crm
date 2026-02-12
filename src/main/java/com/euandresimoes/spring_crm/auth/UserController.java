package com.euandresimoes.spring_crm.auth;

import com.euandresimoes.spring_crm.auth.dto.ProfileResponse;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> role(@AuthenticationPrincipal String id) {
        return ApiResponse.ok(authService.getProfile(UUID.fromString(id)));
    }
}
