package com.picktartup.coinservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinExchangeResponse {
    private Long transactionId;
    private double exchangeAmount;
    private double balanceAfterExchange;
}
