package com.picktartup.coinservice.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name = "cointransaction")
@Entity
public class CoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq_generator")
    @SequenceGenerator(name = "transaction_seq_generator", sequenceName = "transaction_seq", allocationSize = 1)
    @Column(name = "transaction_id")
    @JsonProperty("transactionId")
    private Long transactionId;

    @Column(name = "t_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("tType")
    private TransactionType tType;

    @Column(name = "t_coin_amount", nullable = false)
    @JsonProperty("tCoinAmount")
    private Double tCoinAmount;

    @Column(name = "t_created_at", nullable = false)
    @JsonProperty("tCreatedAt")
    private LocalDateTime tCreatedAt;

    @Column(name = "t_pay_id", length = 50)
    @JsonProperty("tPayId")
    private String tPayId;

    @Column(name = "t_pay_method", length = 30)
    @JsonProperty("tPayMethod")
    private String tPayMethod;

    @Column(name = "t_exc_bank", length = 30)
    @JsonProperty("tExcBank")
    private String tExcBank;

    @Column(name = "t_exc_account", length = 50)
    @JsonProperty("tExcAccount")
    private String tExcAccount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;
}
