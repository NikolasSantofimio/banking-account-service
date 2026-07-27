package com.bankingtest.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountHolder,
        BigDecimal balance,
        LocalDateTime creationDate
) {}