package com.bankingtest.accountservice.controller;

import com.bankingtest.accountservice.dto.*;
import com.bankingtest.accountservice.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = bankAccountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = bankAccountService.deposit(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = bankAccountService.withdraw(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long id) {
        BalanceResponse response = bankAccountService.getBalance(id);
        return ResponseEntity.ok(response);
    }
}