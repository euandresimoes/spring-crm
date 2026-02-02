package com.euandresimoes.spring_crm.auth.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.euandresimoes.spring_crm.auth.application.ProfileService;
import com.euandresimoes.spring_crm.auth.domain.dto.ProfileResponse;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/user")
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> role(
            @AuthenticationPrincipal String id) {
        return ApiResponse.ok(profileService.execute(id));
    }
}

