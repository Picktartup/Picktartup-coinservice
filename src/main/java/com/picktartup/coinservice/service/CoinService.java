package com.picktartup.coinservice.service;

import com.picktartup.coinservice.dto.CoinExchangeResponse;
import com.picktartup.coinservice.dto.CoinPurchaseResponse;
import com.picktartup.coinservice.entity.CoinTransaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CoinService {
    public Double getBalance(Long walletId);
    public CoinPurchaseResponse purchaseCoins(Long walletId, double amount, double coin, String paymentId, String paymentMethod);
    public List<CoinTransaction> getPurchases(Long userId);
    public CoinExchangeResponse exchangeCoins(Long walletId, double exchangeAmount, String exchangeBank, String exchangeAccount);
}
