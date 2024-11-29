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
    private Long userId;
    private double exchangeAmount;
    private String exchangeBank;
    private String exchangeAccount;
    private String walletPassword;
}
