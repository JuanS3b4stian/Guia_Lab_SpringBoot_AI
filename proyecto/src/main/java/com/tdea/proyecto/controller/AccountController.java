package com.tdea.proyecto.controller;

import com.tdea.proyecto.dto.AccountResponse;
import com.tdea.proyecto.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes REST endpoints for bank accounts.
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    /**
     * Creates an account controller.
     *
     * @param accountService service that manages account queries
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Lists all registered accounts.
     *
     * @return account list with HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }
}
