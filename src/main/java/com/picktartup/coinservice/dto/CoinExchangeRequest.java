package com.picktartup.coinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinExchangeRequest {
    private Long walletId;
    private double exchangeAmount;
}
