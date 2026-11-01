package com.tdea.proyecto.repository;

import com.tdea.proyecto.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Provides persistence operations for money transfer transactions.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions where the specified account is either the source
     * or the destination account.
     *
     * @param sourceAccountId account identifier to match as the source account
     * @param destinationAccountId account identifier to match as the destination
     * account
     * @return transactions involving either of the specified accounts
     */
    List<Transaction> findBySourceAccountIdOrDestinationAccountId(
            Long sourceAccountId,
            Long destinationAccountId);
}
