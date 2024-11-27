package com.picktartup.coinservice.repository;

import com.picktartup.coinservice.entity.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    List<CoinTransaction> findByUserId(Long userId);
    Optional<CoinTransaction> findByTransactionId(Long transactionId);
}
