package com.euandresimoes.spring_crm.auth.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.euandresimoes.spring_crm.auth.application.DeleteService;

import com.euandresimoes.spring_crm.shared.web.ApiResponse;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DeleteService deleteService;

    public AdminController(DeleteService deleteService) {
        this.deleteService = deleteService;
    }

    @DeleteMapping("/delete/id/{id}")
    public ApiResponse<String> deleteByID(
            @PathVariable String id) {
        deleteService.deleteByID(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/delete/email/{email}")
    public ApiResponse<String> deleteByEmail(
            @PathVariable String email) {
        deleteService.deleteByEmail(email);
        return ApiResponse.ok(null);
    }
}
