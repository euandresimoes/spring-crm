package com.euandresimoes.spring_crm.organization.transactions;

import com.euandresimoes.spring_crm.organization.transactions.dto.CreateTransactionCommand;
import com.euandresimoes.spring_crm.organization.transactions.dto.TransactionResponse;
import com.euandresimoes.spring_crm.organization.transactions.dto.UpdateTransactionCommand;
import com.euandresimoes.spring_crm.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization/{organizationID}/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ApiResponse<TransactionResponse> createTransaction(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @Valid @RequestBody CreateTransactionCommand command) {
        return ApiResponse.ok(transactionService.createTransaction(UUID.fromString(userId), organizationID, command));
    }

    @GetMapping("/find/all")
    public ApiResponse<List<TransactionResponse>> findTransactions(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @RequestParam int page,
            @RequestParam int size) {
        return ApiResponse.ok(transactionService.findTransactions(UUID.fromString(userId), organizationID, page, size));
    }

    @PutMapping
    public ApiResponse<TransactionResponse> updateTransaction(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @Valid @RequestBody UpdateTransactionCommand command) {
        return ApiResponse.ok(transactionService.updateTransaction(UUID.fromString(userId), organizationID, command));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTransaction(
            @NonNull @AuthenticationPrincipal String userId,
            @PathVariable UUID organizationID,
            @PathVariable UUID id) {
        transactionService.deleteTransaction(UUID.fromString(userId), organizationID, id);
        return ApiResponse.ok(null);
    }
}
