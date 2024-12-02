package com.picktartup.coinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentDto {

    // 결제 콜백 요청/응답
    public static class Callback {
        @Getter
        @Builder
        public static class Request {
            private String transactionId;
            private Long userId;
            private BigDecimal cashAmount;
            private String paymentMethod;
        }

        @Getter
        @Builder
        public static class Response {
            private String transactionHash;
            private String toAddress;
            private BigDecimal amount;
            private String status;
            private LocalDateTime processedAt;
        }
    }

}