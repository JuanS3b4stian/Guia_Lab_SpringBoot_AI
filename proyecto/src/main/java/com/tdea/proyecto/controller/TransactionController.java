package com.tdea.proyecto.controller;

import com.tdea.proyecto.dto.TransactionRequest;
import com.tdea.proyecto.dto.TransactionResponse;
import com.tdea.proyecto.exception.AccountNotFoundException;
import com.tdea.proyecto.exception.InsufficientBalanceException;
import com.tdea.proyecto.exception.TransactionNotFoundException;
import com.tdea.proyecto.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Exposes REST endpoints for money transfer transactions.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Creates a transaction controller.
     *
     * @param transactionService service that manages transfers
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Lists the transaction history.
     *
     * @return all transactions with HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    /**
     * Processes a new money transfer.
     *
     * @param request transfer data
     * @return created transaction with HTTP 201
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> processTransfer(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.processTransfer(request));
    }

    /**
     * Reverts a transaction and restores its account balances.
     *
     * @param id transaction identifier
     * @return empty response with HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revertTransaction(@PathVariable Long id) {
        transactionService.revertTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handles account lookup failures.
     *
     * @param exception account lookup exception
     * @return error response with HTTP 404
     */
    @ExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    /**
     * Handles invalid transfer business rules.
     *
     * @param exception business rule exception
     * @return error response with HTTP 400
     */
    @ExceptionHandler({
            InsufficientBalanceException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}
