package com.euandresimoes.spring_crm.auth.api;

import com.euandresimoes.spring_crm.auth.application.CreateUserService;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserCommand;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserResponse;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CreateUserService createUserService;

    public AuthController(CreateUserService createUserService) {
        this.createUserService = createUserService;
    }

    @PostMapping("/register")
    public ApiResponse<CreateUserResponse> createUser(@RequestBody CreateUserCommand command) {
        CreateUserResponse res = createUserService.execute(command);
        return ApiResponse.ok(res);
    }
}
