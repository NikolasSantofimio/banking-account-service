package com.bankingtest.accountservice.service;

import com.bankingtest.accountservice.dto.*;
import com.bankingtest.accountservice.exception.AccountNotFoundException;
import com.bankingtest.accountservice.exception.InsufficientBalanceException;
import com.bankingtest.accountservice.model.BankAccount;
import com.bankingtest.accountservice.model.Transaction;
import com.bankingtest.accountservice.model.TransactionType;
import com.bankingtest.accountservice.repository.BankAccountRepository;
import com.bankingtest.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        BankAccount account = BankAccount.builder()
                .accountHolder(request.accountHolder())
                .balance(BigDecimal.ZERO)
                .build();

        BankAccount saved = bankAccountRepository.save(account);
        log.info("Account created: id={}, accountHolder={}", saved.getId(), saved.getAccountHolder());

        return toAccountResponse(saved);
    }

    @Override
    @Transactional
    public TransactionResponse deposit(Long accountId, TransactionRequest request) {
        BankAccount account = findAccountOrThrow(accountId);

        account.setBalance(account.getBalance().add(request.amount()));
        bankAccountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .build();
        Transaction saved = transactionRepository.save(transaction);

        log.info("Deposit processed: accountId={}, amount={}, newBalance={}",
                accountId, request.amount(), account.getBalance());

        return toTransactionResponse(saved, account);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long accountId, TransactionRequest request) {
        BankAccount account = findAccountOrThrow(accountId);

        if (account.getBalance().compareTo(request.amount()) < 0) {
            log.warn("Withdrawal rejected - insufficient balance: accountId={}, requested={}, available={}",
                    accountId, request.amount(), account.getBalance());
            throw new InsufficientBalanceException(accountId, request.amount(), account.getBalance());
        }

        account.setBalance(account.getBalance().subtract(request.amount()));
        bankAccountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .bankAccount(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.amount())
                .build();
        Transaction saved = transactionRepository.save(transaction);

        log.info("Withdrawal processed: accountId={}, amount={}, newBalance={}",
                accountId, request.amount(), account.getBalance());

        return toTransactionResponse(saved, account);
    }

    @Override
    public BalanceResponse getBalance(Long accountId) {
        BankAccount account = findAccountOrThrow(accountId);
        return new BalanceResponse(account.getId(), account.getAccountHolder(), account.getBalance());
    }

    private BankAccount findAccountOrThrow(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private AccountResponse toAccountResponse(BankAccount account) {
        return new AccountResponse(account.getId(), account.getAccountHolder(), account.getBalance(), account.getCreationDate());
    }

    private TransactionResponse toTransactionResponse(Transaction transaction, BankAccount account) {
        return new TransactionResponse(
                transaction.getId(),
                account.getId(),
                transaction.getType(),
                transaction.getAmount(),
                account.getBalance(),
                transaction.getDate()
        );
    }
}