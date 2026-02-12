package com.euandresimoes.spring_crm.organization.clients;

import com.euandresimoes.spring_crm.organization.clients.dto.ClientResponse;
import com.euandresimoes.spring_crm.organization.clients.dto.CreateClientCommand;
import com.euandresimoes.spring_crm.organization.clients.dto.UpdateClientCommand;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization/{organizationID}/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ApiResponse<ClientResponse> createClient(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @Valid @RequestBody CreateClientCommand command) {
        return ApiResponse.ok(clientService.createClient(UUID.fromString(userId), organizationID, command));
    }

    @GetMapping("/find/all")
    public ApiResponse<List<ClientResponse>> findClients(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @RequestParam int page,
            @RequestParam int size) {
        return ApiResponse.ok(clientService.findClients(UUID.fromString(userId), organizationID, page, size));
    }

    @PutMapping
    public ApiResponse<ClientResponse> updateClient(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @Valid @RequestBody UpdateClientCommand command) {
        return ApiResponse.ok(clientService.updateClient(UUID.fromString(userId), organizationID, command));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteClient(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @PathVariable UUID id) {
        clientService.deleteClient(UUID.fromString(userId), organizationID, id);
        return ApiResponse.ok(null);
    }
}
