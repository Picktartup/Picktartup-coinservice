package com.picktartup.coinservice.service;

import com.picktartup.coinservice.dto.CoinExchangeResponse;
import com.picktartup.coinservice.dto.CoinPurchaseResponse;
import com.picktartup.coinservice.dto.CoinValidationResponse;
import com.picktartup.coinservice.entity.CoinTransaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CoinService {
    public Double getBalance(Long userId);
    public CoinPurchaseResponse purchaseCoins(Long userId, double amount, double coin, String paymentId, String paymentMethod);
    public List<CoinTransaction> getPurchases(Long userId);
    public CoinExchangeResponse exchangeCoins(Long userId, double exchangeAmount, String exchangeBank, String exchangeAccount);
//    public CoinValidationResponse validatePayment(Long transactionId);
}
