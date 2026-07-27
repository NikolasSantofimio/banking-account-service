package com.bankingtest.accountservice.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId, BigDecimal requested, BigDecimal available) {
        super("Account " + accountId + " has insufficient balance. Requested: "
                + requested + ", available: " + available);
    }
}