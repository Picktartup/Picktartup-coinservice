package com.picktartup.coinservice.mock;

import com.picktartup.coinservice.entity.Role;
import com.picktartup.coinservice.entity.Wallet;
import com.picktartup.coinservice.entity.Users;

import java.time.LocalDateTime;

public class WalletMock {
    public static Wallet createWalletMock() {
        Users users = Users.builder()
                .userId(1L)
                .username("김우리")
                .email("yummytomato7@gmail.com")
                .encryptedPwd("X9as90fs2n09")
                .role(Role.USER)
                .isActivated(true)
                .createdAt(LocalDateTime.now())
                .build();

        return Wallet.builder()
                .walletId(1L)
                .address("0xl2823bfa8sa9xxa3")
                .balance(1000.0)
                .users(users)
                .build();
    }
}
