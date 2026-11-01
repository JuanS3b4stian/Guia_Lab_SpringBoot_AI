package com.tdea.proyecto.exception;

/**
 * Indicates that a requested transaction does not exist.
 */
public class TransactionNotFoundException extends RuntimeException {

    /**
     * Creates an exception for a transaction that cannot be found.
     *
     * @param transactionId missing transaction identifier
     */
    public TransactionNotFoundException(Long transactionId) {
        super("Transaction not found: " + transactionId);
    }
}
