package com.bankingtest.accountservice.dto;

import com.bankingtest.accountservice.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal currentBalance,
        LocalDateTime date
) {}