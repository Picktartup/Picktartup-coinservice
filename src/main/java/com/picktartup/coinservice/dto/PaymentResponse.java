package com.picktartup.coinservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String status;
    private Amount amount;

    @Getter
    public static class Amount {
        private double total;
    }
}
