package com.picktartup.coinservice.service;

import com.picktartup.coinservice.dto.*;
import com.picktartup.coinservice.entity.CoinTransaction;
import com.picktartup.coinservice.entity.TransactionType;
import com.picktartup.coinservice.entity.Users;
import com.picktartup.coinservice.entity.Wallet;
import com.picktartup.coinservice.mock.WalletMock;
import com.picktartup.coinservice.repository.CoinTransactionRepository;
import com.picktartup.coinservice.webclient.WalletServiceClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {

    // 임시
    // Long userId = Long.valueOf("1");

    private final CoinTransactionRepository coinTransactionRepository;
    private final WalletServiceClient walletServiceClient;
    private RestTemplate restTemplate;

    @Value("${PORTONE_API_SECRET}")
    private String PORTONE_API_SECRET;


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
    public Double getBalance(Long userId) {
        return Double.valueOf(walletServiceClient.getWalletBalance(userId));
    }

    // 코인 구매
    @Transactional
    public CoinPurchaseResponse purchaseCoins(Long userId, double amount, double coin, String paymentId, String paymentMethod) {
        // 1. PortOne 결제 검증 요청
        if (!validatePayment(paymentId, amount)) {
            throw new IllegalArgumentException("결제 검증에 실패했습니다.");
        }

        // 2. CoinTransaction 생성
        CoinTransaction transaction = CoinTransaction.builder()
                .tType(TransactionType.PAYMENT)
                .tCoinAmount(coin)
                .tCreatedAt(LocalDateTime.now())
                .userId(userId)
                .tPayId(paymentId)
                .tPayMethod(paymentMethod)
                .build();

        // 3. 거래 정보 저장
        coinTransactionRepository.save(transaction);

        // 4. 결제가 유효하면 코인 구매 처리를 진행
        PaymentDto.Callback.Request tokenChargeRequest = PaymentDto.Callback.Request.builder()
                .transactionId(transaction.getTransactionId())
                .userId(userId)
                .amount(BigDecimal.valueOf(coin))
                .paymentMethod(paymentMethod)
                .build();
        TransactionDto.Response tokenChargeResponse = walletServiceClient.handlePaymentCallback(tokenChargeRequest);

        // 5. 응답 객체 생성 후 반환
        return CoinPurchaseResponse.builder()
                .transactionId(transaction.getTransactionId())
                .coinAmount(coin)
                .walletBalance(tokenChargeResponse.getTotalBalance().doubleValue())
                .build();
    }

    // 코인 구매 내역 조회
    public List<CoinTransaction> getPurchases(Long userId) {
        return coinTransactionRepository.findByUserId(userId);
    }

    // 코인 현금화
    @Transactional
    public CoinExchangeResponse exchangeCoins(Long userId, double exchangeAmount, String exchangeBank, String exchangeAccount) {
        // CoinTransaction 객체 생성 및 저장
        CoinTransaction transaction = CoinTransaction.builder()
                .tType(TransactionType.EXCHANGE)
                .tCoinAmount(exchangeAmount)
                .tCreatedAt(LocalDateTime.now())
                .userId(userId)
                .tExcBank(exchangeBank)
                .tExcAccount(exchangeAccount)
                .build();
        coinTransactionRepository.save(transaction);

        // Wallet 서비스의 토큰 환급 API 호출 (지갑 잔액 반영)
        TransactionDto.Request tokenExchangeRequest = TransactionDto.Request.builder()
                .userId(userId)
                .transactionId(transaction.getTransactionId())
                .amount(exchangeAmount)
                .build();
        TransactionDto.Response tokenExchangeResponse = walletServiceClient.transmitToAdmin(tokenExchangeRequest);

        // 관리자의 현금 발급 수행 로직

        return CoinExchangeResponse.builder()
                .transactionId(transaction.getTransactionId())
                .exchangeAmount(exchangeAmount)
                .balanceAfterExchange(tokenExchangeResponse.getTotalBalance().doubleValue())
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
