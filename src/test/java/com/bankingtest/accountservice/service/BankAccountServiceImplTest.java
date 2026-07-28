package com.bankingtest.accountservice.service;

import com.bankingtest.accountservice.dto.*;
import com.bankingtest.accountservice.exception.AccountNotFoundException;
import com.bankingtest.accountservice.exception.InsufficientBalanceException;
import com.bankingtest.accountservice.model.BankAccount;
import com.bankingtest.accountservice.model.Transaction;
import com.bankingtest.accountservice.model.TransactionType;
import com.bankingtest.accountservice.repository.BankAccountRepository;
import com.bankingtest.accountservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount existingAccount;

    @BeforeEach
    void setUp() {
        existingAccount = BankAccount.builder()
                .id(1L)
                .accountHolder("Saulo Santofimio")
                .balance(new BigDecimal("100000.00"))
                .creationDate(LocalDateTime.now())
                .build();
    }

    // ---------- CREATE ACCOUNT ----------

    @Test
    void createAccount_shouldStartWithZeroBalance() {
        CreateAccountRequest request = new CreateAccountRequest("Saulo Santofimio");

        BankAccount savedAccount = BankAccount.builder()
                .id(1L)
                .accountHolder("Saulo Santofimio")
                .balance(BigDecimal.ZERO)
                .creationDate(LocalDateTime.now())
                .build();

        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savedAccount);

        AccountResponse response = bankAccountService.createAccount(request);

        assertThat(response.accountHolder()).isEqualTo("Saulo Santofimio");
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void createAccount_shouldPersistAccountHolderCorrectly() {
        CreateAccountRequest request = new CreateAccountRequest("Briggitte Angrino");

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = bankAccountService.createAccount(request);

        assertThat(response.accountHolder()).isEqualTo("Briggitte Angrino");
    }

    // ---------- WITHDRAW ----------

    @Test
    void withdraw_shouldSucceedWhenBalanceIsSufficient() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("30000.00"));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(existingAccount);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction t = invocation.getArgument(0);
                    t.setId(10L);
                    return t;
                });

        TransactionResponse response = bankAccountService.withdraw(1L, request);

        assertThat(response.type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(response.currentBalance()).isEqualByComparingTo(new BigDecimal("70000.00"));
        verify(bankAccountRepository).save(existingAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_shouldThrowExceptionWhenBalanceIsInsufficient() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("500000.00"));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));

        assertThatThrownBy(() -> bankAccountService.withdraw(1L, request))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(bankAccountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAccountDoesNotExist() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("1000.00"));

        when(bankAccountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankAccountService.withdraw(99L, request))
                .isInstanceOf(AccountNotFoundException.class);
    }

    // ---------- DEPOSIT (extra) ----------

    @Test
    void deposit_shouldIncreaseBalance() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("50000.00"));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(existingAccount);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction t = invocation.getArgument(0);
                    t.setId(11L);
                    return t;
                });

        TransactionResponse response = bankAccountService.deposit(1L, request);

        assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(response.currentBalance()).isEqualByComparingTo(new BigDecimal("150000.00"));
    }

    // ---------- BALANCE (extra) ----------

    @Test
    void getBalance_shouldReturnCurrentBalance() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));

        BalanceResponse response = bankAccountService.getBalance(1L);

        assertThat(response.balance()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(response.accountHolder()).isEqualTo("Saulo Santofimio");
    }
}