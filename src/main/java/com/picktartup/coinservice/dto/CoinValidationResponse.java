package com.picktartup.coinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinValidationResponse {
    private Long transactionId;
    private Long userId;
    private BigDecimal amount;
}
