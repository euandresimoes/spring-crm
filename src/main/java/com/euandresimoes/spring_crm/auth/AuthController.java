package com.euandresimoes.spring_crm.auth;

import com.euandresimoes.spring_crm.auth.dto.CreateUserCommand;
import com.euandresimoes.spring_crm.auth.dto.CreateUserResponse;
import com.euandresimoes.spring_crm.auth.dto.LoginCommand;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<CreateUserResponse> createUser(@Valid @RequestBody CreateUserCommand command) {
        CreateUserResponse res = authService.createUser(command);
        return ApiResponse.ok(res);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginCommand command) throws Exception {
        String token = authService.login(command);
        return ApiResponse.ok(token);
    }
}
