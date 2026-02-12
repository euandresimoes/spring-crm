package com.euandresimoes.spring_crm.auth;

import com.euandresimoes.spring_crm.auth.dto.FindUserResponse;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/find/id/{id}")
    public ApiResponse<FindUserResponse> findByID(@NonNull @PathVariable UUID id) {
        return ApiResponse.ok(authService.findUserByID(id));
    }

    @GetMapping("/find/email/{email}")
    public ApiResponse<FindUserResponse> findByEmail(@PathVariable String email) {
        return ApiResponse.ok(authService.findUserByEmail(email));
    }

    @GetMapping("/find/all")
    public ApiResponse<List<FindUserResponse>> findAll(
            @RequestParam int page,
            @RequestParam int size) {
        return ApiResponse.ok(authService.findAllUsers(page, size));
    }

    @DeleteMapping("/delete/id/{id}")
    public ApiResponse<String> deleteByID(@NonNull @PathVariable UUID id) {
        authService.deleteUserByID(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/delete/email/{email}")
    public ApiResponse<String> deleteByEmail(@PathVariable String email) {
        authService.deleteUserByEmail(email);
        return ApiResponse.ok(null);
    }
}
