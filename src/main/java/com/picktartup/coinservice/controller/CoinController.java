package com.picktartup.coinservice.controller;

import com.picktartup.coinservice.common.dto.ApiResponse;
import com.picktartup.coinservice.dto.CoinExchangeRequest;
import com.picktartup.coinservice.dto.CoinExchangeResponse;
import com.picktartup.coinservice.dto.CoinPurchaseRequest;
import com.picktartup.coinservice.dto.CoinPurchaseResponse;
import com.picktartup.coinservice.entity.CoinTransaction;
import com.picktartup.coinservice.service.CoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coins")
public class CoinController {
    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @GetMapping("/health_check")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.ok("서비스가 정상 작동 중입니다."));
    }

    // 잔여 코인 조회
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Double>> getBalance(@RequestParam Long walletId) {
        Double balance = coinService.getBalance(walletId);
        return ResponseEntity.ok(ApiResponse.ok(balance));
    }

    // 코인 구매
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<CoinPurchaseResponse>> purchaseCoins(@RequestBody CoinPurchaseRequest request) {
        CoinPurchaseResponse response = coinService.purchaseCoins(request.getWalletId(), request.getPaymentId(), request.getAmount());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 코인 구매 내역 조회
    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<CoinTransaction>>> getPurchases(@RequestParam Long userId) {
        List<CoinTransaction> purchases = coinService.getPurchases(userId);
        return ResponseEntity.ok(ApiResponse.ok(purchases));
    }

    // 코인 현금화
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<CoinExchangeResponse>> exchangeCoins(@RequestBody CoinExchangeRequest request) {
        CoinExchangeResponse response = coinService.exchangeCoins(request.getWalletId(), request.getExchangeAmount());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
