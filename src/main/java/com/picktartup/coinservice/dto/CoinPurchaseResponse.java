package com.picktartup.coinservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinPurchaseResponse {
    private Long transactionId;
    private double coinAmount;
    private double walletBalance;
}