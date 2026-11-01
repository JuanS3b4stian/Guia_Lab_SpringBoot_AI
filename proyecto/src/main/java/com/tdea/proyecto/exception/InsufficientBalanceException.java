package com.tdea.proyecto.exception;

import java.math.BigDecimal;

/**
 * Indicates that an account does not have enough balance for a transfer.
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Creates an exception for an account with insufficient balance.
     *
     * @param accountId account identifier with insufficient balance
     * @param requestedAmount amount requested by the transfer
     */
    public InsufficientBalanceException(Long accountId, BigDecimal requestedAmount) {
        super("Insufficient balance in account " + accountId
                + " for amount " + requestedAmount);
    }
}
