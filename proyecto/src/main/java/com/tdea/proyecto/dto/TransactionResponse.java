package com.tdea.proyecto.dto;

import com.tdea.proyecto.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction returned by the API.
 *
 * @param id transaction identifier
 * @param sourceAccountId debited account identifier
 * @param destinationAccountId credited account identifier
 * @param amount transferred amount
 * @param transactionDate transaction date and time
 * @param status transaction status
 * @param qrCodeBase64 Base64-encoded PNG QR code with transaction data
 */
public record TransactionResponse(
        Long id,
        Long sourceAccountId,
        Long destinationAccountId,
        BigDecimal amount,
        LocalDateTime transactionDate,
        TransactionStatus status,
        String qrCodeBase64) {
}
