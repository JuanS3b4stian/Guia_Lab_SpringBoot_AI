package com.tdea.proyecto.exception;

/**
 * Indicates that a requested bank account does not exist.
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Creates an exception for an account that cannot be found.
     *
     * @param accountId missing account identifier
     */
    public AccountNotFoundException(Long accountId) {
        super("Account not found: " + accountId);
    }
}
