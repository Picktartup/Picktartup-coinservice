package com.picktartup.coinservice.webclient;

import com.picktartup.coinservice.dto.WalletDto;
import com.picktartup.coinservice.dto.response.BaseResponse;
import com.picktartup.coinservice.exception.BusinessException;
import com.picktartup.coinservice.exception.ErrorCode;
import com.picktartup.coinservice.dto.PaymentDto;
import com.picktartup.coinservice.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class WalletServiceClient {

    private final WebClient walletServiceWebClient;

    // 잔여 코인 조회
    public double getWalletBalance(Long userId) {
        try {
            // Wallet Service API 호출
            BaseResponse<WalletDto.Balance.Response> response = walletServiceWebClient.get()
                    .uri("/api/v1/wallets/users/" + userId + "/balance")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> handle4xxErrors(clientResponse.statusCode()))
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            Mono.error(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Wallet Service Internal Error")))
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<WalletDto.Balance.Response>>() {})
                    .block(); // 동기식 호출

            // 응답 검증 및 balance 값만 반환
            if (response == null || response.getData() == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Invalid response from Wallet Service");
            }

            return Double.parseDouble(response.getData().getBalance());

        } catch (Exception e) {
            log.error("Error calling Wallet Service for userId {}: {}", userId, e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 결제 완료 콜백 처리
    public TransactionDto.Response handlePaymentCallback(PaymentDto.Callback.Request request) {
        try {
            // Wallet Service API 호출
            BaseResponse<TransactionDto.Response> response = walletServiceWebClient.post()
                    .uri("/api/v1/wallets/payment/callback")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> handle4xxErrors(clientResponse.statusCode()))
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            Mono.error(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Wallet Service Internal Error")))
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<TransactionDto.Response>>() {})
                    .block(); // 동기식 호출

            // 응답 검증
            if (response == null || response.getData() == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Invalid response from Wallet Service");
            }

            return response.getData();

        } catch (Exception e) {
            log.error("Error calling Wallet Service for transactionId {}: {}", request.getTransactionId(), e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 관리자 지갑으로 토큰 전송
    public TransactionDto.Response transmitToAdmin(TransactionDto.Request request) {
        try {
            BaseResponse<TransactionDto.Response> response = walletServiceWebClient.post()
                    .uri("/api/v1/wallets/transmission/admin")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> handle4xxErrors(clientResponse.statusCode()))
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            Mono.error(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Wallet Service Internal Error")))
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<TransactionDto.Response>>() {})
                    .block(); // 동기식 호출

            if (response == null || response.getData() == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Invalid response from Wallet Service");
            }

            return response.getData();

        } catch (Exception e) {
            log.error("Error calling Wallet Service for admin transmission: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Mono<Throwable> handle4xxErrors(HttpStatusCode statusCode) {
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        if (httpStatus == HttpStatus.NOT_FOUND) {
            return Mono.error(new BusinessException(ErrorCode.WALLET_NOT_FOUND, "Wallet not found"));
        }
        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            return Mono.error(new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "Unauthorized access to wallet"));
        }
        if (httpStatus == HttpStatus.BAD_REQUEST) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Invalid request to Wallet Service"));
        }
        return Mono.error(new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Client error occurred"));
    }
}
