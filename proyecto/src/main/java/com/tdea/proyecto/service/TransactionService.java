package com.tdea.proyecto.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tdea.proyecto.dto.TransactionRequest;
import com.tdea.proyecto.dto.TransactionResponse;
import com.tdea.proyecto.entity.Account;
import com.tdea.proyecto.entity.Transaction;
import com.tdea.proyecto.entity.TransactionStatus;
import com.tdea.proyecto.exception.AccountNotFoundException;
import com.tdea.proyecto.exception.InsufficientBalanceException;
import com.tdea.proyecto.exception.TransactionNotFoundException;
import com.tdea.proyecto.repository.AccountRepository;
import com.tdea.proyecto.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Implements the business operations for money transfers.
 */
@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Creates a transaction service with its persistence dependencies.
     *
     * @param accountRepository account persistence repository
     * @param transactionRepository transaction persistence repository
     */
    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Returns the complete transaction history.
     *
     * @return all persisted transactions
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Processes a transfer atomically and generates its QR code.
     *
     * @param request transfer source, destination and amount
     * @return the completed transfer including its Base64 QR code
     * @throws AccountNotFoundException when either account does not exist
     * @throws InsufficientBalanceException when the source account lacks funds
     */
    @Transactional
    public TransactionResponse processTransfer(TransactionRequest request) {
        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must differ");
        }

        Account sourceAccount = findAccount(request.sourceAccountId());
        Account destinationAccount = findAccount(request.destinationAccountId());

        if (sourceAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException(sourceAccount.getId(), request.amount());
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.amount()));

        Transaction transaction = new Transaction(
                sourceAccount,
                destinationAccount,
                request.amount(),
                LocalDateTime.now(),
                TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return toResponse(savedTransaction);
    }

    /**
     * Reverts a completed transfer and restores both account balances.
     *
     * @param transactionId transaction identifier to revert
     * @throws TransactionNotFoundException when the transaction does not exist
     * @throws IllegalStateException when the transaction is not completed
     */
    @Transactional
    public void revertTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Only completed transactions can be reverted");
        }

        Account sourceAccount = transaction.getSourceAccount();
        Account destinationAccount = transaction.getDestinationAccount();
        sourceAccount.setBalance(sourceAccount.getBalance().add(transaction.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().subtract(transaction.getAmount()));
        transaction.setStatus(TransactionStatus.FAILED);
        transactionRepository.save(transaction);
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private TransactionResponse toResponse(Transaction transaction) {
        String qrCode = transaction.getStatus() == TransactionStatus.COMPLETED
                ? generateQrCode(transaction)
                : null;
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSourceAccount().getId(),
                transaction.getDestinationAccount().getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getStatus(),
                qrCode);
    }

    private String generateQrCode(Transaction transaction) {
        String content = "transactionId=" + transaction.getId()
                + ";sourceAccountId=" + transaction.getSourceAccount().getId()
                + ";destinationAccountId=" + transaction.getDestinationAccount().getId()
                + ";amount=" + transaction.getAmount()
                + ";date=" + transaction.getTransactionDate();
        try {
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", output);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (com.google.zxing.WriterException | IOException exception) {
            throw new IllegalStateException("Unable to generate transaction QR code", exception);
        }
    }
}
