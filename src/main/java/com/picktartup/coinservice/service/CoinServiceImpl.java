package com.picktartup.coinservice.service;

import com.picktartup.coinservice.dto.CoinExchangeResponse;
import com.picktartup.coinservice.dto.CoinPurchaseResponse;
import com.picktartup.coinservice.entity.CoinTransaction;
import com.picktartup.coinservice.entity.TransactionType;
import com.picktartup.coinservice.entity.Users;
import com.picktartup.coinservice.entity.Wallet;
import com.picktartup.coinservice.repository.CoinTransactionRepository;
import com.picktartup.coinservice.repository.UsersRepository;
import com.picktartup.coinservice.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CoinServiceImpl implements CoinService {

    // 임시
    Long userId = Long.valueOf("1");

    @Autowired
    private final UsersRepository usersRepository;
    @Autowired
    private final WalletRepository walletRepository;
    @Autowired
    private final CoinTransactionRepository coinTransactionRepository;

    public CoinServiceImpl(UsersRepository usersRepository, WalletRepository walletRepository, CoinTransactionRepository coinTransactionRepository) {
        this.usersRepository = usersRepository;
        this.walletRepository = walletRepository;
        this.coinTransactionRepository = coinTransactionRepository;
    }

    // 잔여 코인 조회
    public Double getBalance(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("지갑을 찾을 수 없습니다."));
        return wallet.getBalance();
    }

    // 코인 구매
    @Transactional
    public CoinPurchaseResponse purchaseCoins(Long walletId, double amount) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) {
            throw new IllegalArgumentException("지갑을 찾을 수 없습니다.");
        }
        Wallet wallet = walletOpt.get();

        // 코인 구매 로직

        // 지갑 잔액 반영
        wallet.setBalance(wallet.getBalance() + amount);

        // CoinTransaction 객체 생성
        CoinTransaction transaction = CoinTransaction.builder()
                .transactionType(TransactionType.valueOf("PAYMENT"))
                .coinAmount(amount)
                .createdAt(LocalDateTime.now())
                .users(user)
                .build();
        coinTransactionRepository.save(transaction);

        // 성공 응답 데이터 생성
        return CoinPurchaseResponse.builder()
                .transactionId(transaction.getTransactionId())
                .coinAmount(amount)
                .walletBalance(wallet.getBalance())
                .build();
    }

    // 코인 구매 내역 조회
    public List<CoinTransaction> getPurchases(Long userId) {
        return coinTransactionRepository.findByUsers_UserId(userId);

    }

    // 코인 현금화
    @Transactional
    public CoinExchangeResponse exchangeCoins(Long walletId, double exchangeAmount) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) {
            throw new IllegalArgumentException("지갑을 찾을 수 없습니다.");
        }
        Wallet wallet = walletOpt.get();

        if (wallet.getBalance() < exchangeAmount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 코인 현금화 로직

        // 지갑 잔액 반영
        double balanceBefore = wallet.getBalance();
        wallet.setBalance(balanceBefore - exchangeAmount);

        // CoinTransaction 객체 생성
        CoinTransaction transaction = CoinTransaction.builder()
                .transactionType(TransactionType.valueOf("EXCHANGE"))
                .coinAmount(exchangeAmount)
                .createdAt(LocalDateTime.now())
                .users(user)
                .build();
        coinTransactionRepository.save(transaction);

        return CoinExchangeResponse.builder()
                .transactionId(transaction.getTransactionId())
                .exchangeAmount(exchangeAmount)
                .balanceBeforeExchange(balanceBefore)
                .balanceAfterExchange(wallet.getBalance())
                .build();
    }
}
