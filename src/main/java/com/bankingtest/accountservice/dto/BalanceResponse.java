package com.bankingtest.accountservice.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        Long accountId,
        String accountHolder,
        BigDecimal balance
) {}