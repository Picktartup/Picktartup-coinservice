package com.picktartup.coinservice.entity;


import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name = "wallet")
@Entity
public class Wallet {

    @Id
    @Column(name = "wallet_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq_generator")
    @SequenceGenerator(name = "wallet_seq_generator", sequenceName = "wallet_seq", allocationSize = 1)
    private Long walletId;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "balance", nullable = false)
    private Double balance;

}
