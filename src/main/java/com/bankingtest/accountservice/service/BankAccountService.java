package com.bankingtest.accountservice.service;

import com.bankingtest.accountservice.dto.*;

public interface BankAccountService {
    AccountResponse createAccount(CreateAccountRequest request);
    TransactionResponse deposit(Long accountId, TransactionRequest request);
    TransactionResponse withdraw(Long accountId, TransactionRequest request);
    BalanceResponse getBalance(Long accountId);
}