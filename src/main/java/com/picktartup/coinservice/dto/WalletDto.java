package com.picktartup.coinservice.dto;

import lombok.Builder;
import lombok.Getter;

public class WalletDto {

    public static class Balance {
        @Getter
        @Builder
        public static class Response {
            private Long userId;
            private String address;
            private String balance;
        }
    }

}
