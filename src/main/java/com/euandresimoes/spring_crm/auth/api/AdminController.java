package com.euandresimoes.spring_crm.auth.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.euandresimoes.spring_crm.auth.application.DeleteService;
import com.euandresimoes.spring_crm.auth.application.FindService;
import com.euandresimoes.spring_crm.auth.domain.dto.FindUserResponse;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final FindService findService;
    private final DeleteService deleteService;

    public AdminController(FindService findService, DeleteService deleteService) {
        this.findService = findService;
        this.deleteService = deleteService;
    }

    @GetMapping("/find/id/{id}")
    public ApiResponse<FindUserResponse> findByID(@PathVariable String id) {
        return ApiResponse.ok(findService.findByID(id));
    }

    @GetMapping("/find/email/{email}")
    public ApiResponse<FindUserResponse> findByEmail(@PathVariable String email) {
        return ApiResponse.ok(findService.findByEmail(email));
    }

    @GetMapping("/find/all")
    public ApiResponse<List<FindUserResponse>> findAll(
            @RequestParam int page,
            @RequestParam int size) {
        return ApiResponse.ok(findService.findAll(page, size));
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
