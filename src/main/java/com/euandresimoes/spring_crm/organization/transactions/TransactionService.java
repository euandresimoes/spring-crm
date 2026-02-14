package com.euandresimoes.spring_crm.organization.transactions;

import com.euandresimoes.spring_crm.organization.organization_core.OrganizationEntity;
import com.euandresimoes.spring_crm.organization.organization_core.OrganizationRepository;
import com.euandresimoes.spring_crm.organization.organization_core.exception.OrganizationNotFoundException;
import com.euandresimoes.spring_crm.organization.transactions.dto.CreateTransactionCommand;
import com.euandresimoes.spring_crm.organization.transactions.dto.TransactionResponse;
import com.euandresimoes.spring_crm.organization.transactions.dto.UpdateTransactionCommand;
import com.euandresimoes.spring_crm.organization.transactions.exception.TransactionNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final OrganizationRepository organizationRepo;

    public TransactionService(TransactionRepository transactionRepo, OrganizationRepository organizationRepo) {
        this.transactionRepo = transactionRepo;
        this.organizationRepo = organizationRepo;
    }

    public TransactionResponse createTransaction(UUID userId, UUID organizationID, CreateTransactionCommand command) {
        OrganizationEntity org = organizationRepo.findByIdAndUserId(organizationID, userId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationID));

        TransactionEntity transaction = new TransactionEntity(
                org,
                userId,
                command.description(),
                command.amount(),
                command.type());

        transactionRepo.save(transaction);
        return TransactionResponse.from(transaction);
    }

    public void deleteTransaction(UUID userId, UUID organizationID, UUID id) {
        transactionRepo.deleteByIdAndUserIdAndOrganization_Id(
                id,
                userId,
                organizationID);
    }

    public List<TransactionResponse> findTransactions(UUID userId, UUID organizationID, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> pageResult = transactionRepo.findAllByUserIdAndOrganization_Id(
                userId,
                organizationID,
                pageable);

        return pageResult
                .getContent()
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    public TransactionResponse updateTransaction(UUID userId, UUID organizationID, UpdateTransactionCommand command) {
        TransactionEntity transaction = transactionRepo.findByIdAndUserIdAndOrganization_Id(
                command.id(),
                userId,
                organizationID)
                .orElseThrow(() -> new TransactionNotFoundException(command.id()));

        transaction.setDescription(command.description());
        transaction.setAmount(command.amount());
        transaction.setType(command.type());

        return TransactionResponse.from(transactionRepo.save(transaction));
    }
}
