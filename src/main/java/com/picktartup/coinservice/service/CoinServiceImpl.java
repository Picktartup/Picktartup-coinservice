package com.picktartup.coinservice.service;

import com.picktartup.coinservice.dto.CoinExchangeResponse;
import com.picktartup.coinservice.dto.CoinPurchaseResponse;
import com.picktartup.coinservice.dto.CoinValidationResponse;
import com.picktartup.coinservice.dto.PaymentResponse;
import com.picktartup.coinservice.entity.CoinTransaction;
import com.picktartup.coinservice.entity.TransactionType;
import com.picktartup.coinservice.entity.Users;
import com.picktartup.coinservice.entity.Wallet;
import com.picktartup.coinservice.mock.WalletMock;
import com.picktartup.coinservice.repository.CoinTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CoinServiceImpl implements CoinService {

    // 임시
    Long userId = Long.valueOf("1");

    @Autowired
    private final CoinTransactionRepository coinTransactionRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${PORTONE_API_SECRET}")
    private String PORTONE_API_SECRET;

    Wallet walletMock = WalletMock.createWalletMock();

    public CoinServiceImpl(CoinTransactionRepository coinTransactionRepository) {
        this.coinTransactionRepository = coinTransactionRepository;
    }

    // PortOne 결제 검증 메서드
    private boolean validatePayment(String paymentId, double expectedAmount) {
        String url = "https://api.portone.io/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "PortOne " + PORTONE_API_SECRET);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<PaymentResponse> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PaymentResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to retrieve payment data");
        }

        PaymentResponse payment = response.getBody();
        // 결제 상태가 "PAID"이고 금액이 예상 금액과 일치할 때만 true 반환
        return "PAID".equals(payment.getStatus()) && payment.getAmount().getTotal() == expectedAmount;
    }

    // 잔여 코인 조회
    public Double getBalance(Long walletId) {
        return walletMock.getBalance();
    }

    // 코인 구매
    @Transactional
    public CoinPurchaseResponse purchaseCoins(Long walletId, double amount, double coin, String paymentId, String paymentMethod) {
        // 1. PortOne 결제 검증 요청
        if (!validatePayment(paymentId, amount)) {
            throw new IllegalArgumentException("결제 검증에 실패했습니다.");
        }

        // 2. 결제가 유효하면 코인 구매 처리를 진행
        walletMock.setBalance(walletMock.getBalance() + coin); // 지갑 잔액 업데이트

        // 3. 거래 정보 저장
        CoinTransaction transaction = CoinTransaction.builder()
                .tType(TransactionType.PAYMENT)
                .tCoinAmount(coin)
                .tCreatedAt(LocalDateTime.now())
                .userId(walletMock.getUsers().getUserId())
                .tPayId(paymentId)
                .tPayMethod(paymentMethod)
                .build();
        coinTransactionRepository.save(transaction);

        // 4. 응답 객체 생성 후 반환
        return CoinPurchaseResponse.builder()
                .transactionId(transaction.getTransactionId())
                .coinAmount(coin)
                .walletBalance(walletMock.getBalance())
                .build();
    }

    // 코인 구매 내역 조회
    public List<CoinTransaction> getPurchases(Long userId) {
        return coinTransactionRepository.findByUserId(userId);
    }

    // 코인 현금화
    @Transactional
    public CoinExchangeResponse exchangeCoins(Long walletId, double exchangeAmount, String exchangeBank, String exchangeAccount) {
        if (walletMock.getBalance() < exchangeAmount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 코인 현금화 로직

        // 지갑 잔액 반영
        double balanceBefore = walletMock.getBalance();
        walletMock.setBalance(balanceBefore - exchangeAmount);

        // CoinTransaction 객체 생성
        CoinTransaction transaction = CoinTransaction.builder()
                .tType(TransactionType.EXCHANGE)
                .tCoinAmount(exchangeAmount)
                .tCreatedAt(LocalDateTime.now())
                .userId(walletMock.getUsers().getUserId())
                .tExcBank(exchangeBank)
                .tExcAccount(exchangeAccount)
                .build();
        coinTransactionRepository.save(transaction);

        return CoinExchangeResponse.builder()
                .transactionId(transaction.getTransactionId())
                .exchangeAmount(exchangeAmount)
                .balanceBeforeExchange(balanceBefore)
                .balanceAfterExchange(walletMock.getBalance())
                .build();
    }

    public CoinValidationResponse validatePayment(Long transactionId) {
        CoinTransaction transaction = coinTransactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> {
                    return new NoSuchElementException("거래내역을 찾을 수 없습니다.");
                });

        return CoinValidationResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .amount(BigDecimal.valueOf(transaction.getTCoinAmount()))
                .build();
    }
}
