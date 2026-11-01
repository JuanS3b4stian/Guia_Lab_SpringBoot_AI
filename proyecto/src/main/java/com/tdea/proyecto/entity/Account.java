package com.tdea.proyecto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Represents a bank account that can send and receive money transfers.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    /**
     * Creates an empty account for JPA.
     */
    protected Account() {
    }

    /**
     * Creates an account with the provided details.
     *
     * @param accountNumber unique account number
     * @param ownerName account owner's name
     * @param balance account's initial balance
     */
    public Account(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    /**
     * Returns the account identifier.
     *
     * @return account identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Updates the account identifier.
     *
     * @param id account identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the account number.
     *
     * @return account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Updates the account number.
     *
     * @param accountNumber unique account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Returns the account owner's name.
     *
     * @return account owner's name
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Updates the account owner's name.
     *
     * @param ownerName account owner's name
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Returns the account balance.
     *
     * @return account balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Updates the account balance.
     *
     * @param balance account balance
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
