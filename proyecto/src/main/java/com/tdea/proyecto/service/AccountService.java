package com.tdea.proyecto.service;

import com.tdea.proyecto.dto.AccountResponse;
import com.tdea.proyecto.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Provides business operations for bank accounts.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Creates an account service with its persistence dependency.
     *
     * @param accountRepository account persistence repository
     */
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Returns all registered bank accounts.
     *
     * @return account data mapped to API response DTOs
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getOwnerName(),
                        account.getBalance()))
                .toList();
    }
}
