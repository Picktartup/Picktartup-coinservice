package com.picktartup.coinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinPurchaseRequest {
    private Long userId;
    private double amount;
    private double coin;
    private String paymentId;
    private String paymentMethod;
}