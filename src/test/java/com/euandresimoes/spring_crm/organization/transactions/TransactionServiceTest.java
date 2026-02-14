package com.euandresimoes.spring_crm.organization.transactions;

import com.euandresimoes.spring_crm.organization.organization_core.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.OrganizationRepository;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
import com.euandresimoes.spring_crm.organization.transactions.dto.CreateTransactionCommand;
import com.euandresimoes.spring_crm.organization.transactions.dto.TransactionResponse;
import com.euandresimoes.spring_crm.organization.transactions.dto.UpdateTransactionCommand;
import com.euandresimoes.spring_crm.organization.transactions.exception.TransactionNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TransactionService.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private OrganizationRepository organizationRepo;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create a transaction successfully")
    void shouldCreateTransactionSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        CreateTransactionCommand command = new CreateTransactionCommand(
                "Test Transaction",
                new BigDecimal("100.00"),
                TransactionType.INCOME);

        OrganizationEntity organization = new OrganizationEntity(userId, "Organization Name");
        organization.setId(orgId);

        when(organizationRepo.findByIdAndUserId(orgId, userId)).thenReturn(Optional.of(organization));

        TransactionResponse response = transactionService.createTransaction(userId, orgId, command);

        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo(command.description());
        assertThat(response.amount()).isEqualTo(command.amount());
        assertThat(response.type()).isEqualTo(command.type().name());

        verify(organizationRepo).findByIdAndUserId(orgId, userId);
        verify(transactionRepo).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Should throw OrganizationNotFoundException when creating a transaction for a non-existent organization")
    void shouldThrowExceptionWhenOrganizationNotFound() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        CreateTransactionCommand command = new CreateTransactionCommand(
                "Test Transaction",
                new BigDecimal("100.00"),
                TransactionType.INCOME);

        when(organizationRepo.findByIdAndUserId(orgId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(userId, orgId, command))
                .isInstanceOf(OrganizationNotFoundException.class);

        verify(organizationRepo).findByIdAndUserId(orgId, userId);
        verify(transactionRepo, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Should find transactions with pagination")
    void shouldFindTransactionsWithPagination() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        OrganizationEntity organization = new OrganizationEntity(userId, "Organization Name");
        organization.setId(orgId);

        TransactionEntity transaction = new TransactionEntity(
                organization,
                userId,
                "Test Transaction",
                new BigDecimal("100.00"),
                TransactionType.INCOME);
        transaction.setId(UUID.randomUUID());
        transaction.setCreatedAt(Instant.now());

        Page<TransactionEntity> pageResult = new PageImpl<>(List.of(transaction));

        when(transactionRepo.findAllByUserIdAndOrganization_Id(eq(userId), eq(orgId), any(Pageable.class)))
                .thenReturn(pageResult);

        List<TransactionResponse> responses = transactionService.findTransactions(userId, orgId, page, size);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).description()).isEqualTo("Test Transaction");
        verify(transactionRepo).findAllByUserIdAndOrganization_Id(eq(userId), eq(orgId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should update a transaction successfully")
    void shouldUpdateTransactionSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UpdateTransactionCommand command = new UpdateTransactionCommand(
                transactionId,
                "Updated Description",
                new BigDecimal("200.00"),
                TransactionType.EXPENSE);

        OrganizationEntity organization = new OrganizationEntity(userId, "Organization Name");
        organization.setId(orgId);

        TransactionEntity existingTransaction = new TransactionEntity(
                organization,
                userId,
                "Old Description",
                new BigDecimal("100.00"),
                TransactionType.INCOME);
        existingTransaction.setId(transactionId);
        existingTransaction.setCreatedAt(Instant.now());

        when(transactionRepo.findByIdAndUserIdAndOrganization_Id(transactionId, userId, orgId))
                .thenReturn(Optional.of(existingTransaction));
        when(transactionRepo.save(any(TransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.updateTransaction(userId, orgId, command);

        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo("Updated Description");
        assertThat(response.amount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(response.type()).isEqualTo("EXPENSE");

        verify(transactionRepo).findByIdAndUserIdAndOrganization_Id(transactionId, userId, orgId);
        verify(transactionRepo).save(existingTransaction);
    }

    @Test
    @DisplayName("Should throw TransactionNotFoundException when updating a non-existent transaction")
    void shouldThrowExceptionWhenUpdatingNonExistentTransaction() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UpdateTransactionCommand command = new UpdateTransactionCommand(
                transactionId,
                "Updated Description",
                new BigDecimal("200.00"),
                TransactionType.EXPENSE);

        when(transactionRepo.findByIdAndUserIdAndOrganization_Id(transactionId, userId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(userId, orgId, command))
                .isInstanceOf(TransactionNotFoundException.class);

        verify(transactionRepo).findByIdAndUserIdAndOrganization_Id(transactionId, userId, orgId);
        verify(transactionRepo, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Should call repository to delete a transaction")
    void shouldDeleteTransactionSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        transactionService.deleteTransaction(userId, orgId, transactionId);

        verify(transactionRepo).deleteByIdAndUserIdAndOrganization_Id(transactionId, userId, orgId);
    }
}
