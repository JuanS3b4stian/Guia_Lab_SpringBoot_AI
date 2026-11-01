package com.tdea.proyecto.dto;

import java.math.BigDecimal;

/**
 * Represents a bank account exposed by the API.
 *
 * @param id account identifier
 * @param accountNumber unique account number
 * @param ownerName account owner's name
 * @param balance current account balance
 */
public record AccountResponse(
        Long id,
        String accountNumber,
        String ownerName,
        BigDecimal balance) {
}
