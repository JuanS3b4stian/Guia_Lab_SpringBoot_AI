package com.tdea.proyecto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Contains the data required to process a money transfer.
 *
 * @param sourceAccountId account identifier to debit
 * @param destinationAccountId account identifier to credit
 * @param amount amount to transfer
 */
public record TransactionRequest(
        @NotNull Long sourceAccountId,
        @NotNull Long destinationAccountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount) {
}
