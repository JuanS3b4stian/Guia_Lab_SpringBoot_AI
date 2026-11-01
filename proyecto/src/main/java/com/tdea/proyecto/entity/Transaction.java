package com.tdea.proyecto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a money transfer between two bank accounts.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    /**
     * Creates an empty transaction for JPA.
     */
    protected Transaction() {
    }

    /**
     * Creates a transaction with the provided transfer details.
     *
     * @param sourceAccount account to debit
     * @param destinationAccount account to credit
     * @param amount amount to transfer
     * @param transactionDate date and time of the transfer
     * @param status transaction status
     */
    public Transaction(
            Account sourceAccount,
            Account destinationAccount,
            BigDecimal amount,
            LocalDateTime transactionDate,
            TransactionStatus status) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    /**
     * Returns the transaction identifier.
     *
     * @return transaction identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Updates the transaction identifier.
     *
     * @param id transaction identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the source account.
     *
     * @return source account
     */
    public Account getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Updates the source account.
     *
     * @param sourceAccount account to debit
     */
    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    /**
     * Returns the destination account.
     *
     * @return destination account
     */
    public Account getDestinationAccount() {
        return destinationAccount;
    }

    /**
     * Updates the destination account.
     *
     * @param destinationAccount account to credit
     */
    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    /**
     * Returns the transfer amount.
     *
     * @return transfer amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Updates the transfer amount.
     *
     * @param amount transfer amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Returns the transaction date and time.
     *
     * @return transaction date and time
     */
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    /**
     * Updates the transaction date and time.
     *
     * @param transactionDate transaction date and time
     */
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Returns the transaction status.
     *
     * @return transaction status
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Updates the transaction status.
     *
     * @param status transaction status
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
