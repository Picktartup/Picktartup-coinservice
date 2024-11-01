package com.picktartup.coin.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Table(name ="startup")
@Entity
public class Startup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "startup_seq_generator")
    @SequenceGenerator(name = "startup_seq_generator", sequenceName = "startup_seq", allocationSize = 1)
    @Column(name = "startup_id")
    private Long startupId;

    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    private String name;
    private String description;
    private String category;
    private String progress;
    private Double ssi;
    private LocalDateTime contractDeadline;
    private LocalDateTime contractTargetDeadline;
    private Integer goalCoin;
    private Double expectedReturn;

    @OneToMany(mappedBy = "startup", cascade = CascadeType.ALL)
    private Set<Contract> contracts;
}
