package com.euandresimoes.spring_crm.auth.api;

import com.euandresimoes.spring_crm.auth.application.CreateUserService;
import com.euandresimoes.spring_crm.auth.application.LoginService;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserCommand;
import com.euandresimoes.spring_crm.auth.domain.dto.CreateUserResponse;
import com.euandresimoes.spring_crm.auth.domain.dto.LoginCommand;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CreateUserService createUserService;
    private final LoginService loginService;

    public AuthController(CreateUserService createUserService, LoginService loginService) {
        this.createUserService = createUserService;
        this.loginService = loginService;
    }

    @PostMapping("/register")
    public ApiResponse<CreateUserResponse> createUser(@RequestBody CreateUserCommand command) {
        CreateUserResponse res = createUserService.execute(command);
        return ApiResponse.ok(res);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginCommand command) throws Exception {
        String token = loginService.execute(command);
        return ApiResponse.ok(token);
    }
    
}
