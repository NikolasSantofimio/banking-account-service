package com.bankingtest.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank(message = "accountHolder is required")
        @Size(min = 1, max = 100, message = "accountHolder must be between 1 and 100 characters")
        String accountHolder
) {}