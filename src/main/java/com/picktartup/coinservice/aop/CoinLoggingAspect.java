package com.picktartup.coinservice.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CoinLoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. 코인 구매 모니터링
    @Around("execution(* com.picktartup.coinservice.service.CoinServiceImpl.purchaseCoins(..))")
    public Object monitorCoinPurchase(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime purchaseTime = LocalDateTime.now();
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        double amount = (double) args[1];
        double coin = (double) args[2];
        String paymentMethod = (String) args[4];

        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "coin_purchase");
        logData.put("user_id", userId);
        logData.put("cash_amount", amount);
        logData.put("coin_amount", coin);
        logData.put("payment_method", paymentMethod);
        logData.put("timestamp", purchaseTime.toString());
        logData.put("hour", purchaseTime.getHour());
        logData.put("day_of_week", purchaseTime.getDayOfWeek().toString());

        try {
            Object result = joinPoint.proceed();
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;
        } catch (Exception e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }

    // 2. 코인 현금화 모니터링
    @Around("execution(* com.picktartup.coinservice.service.CoinServiceImpl.exchangeCoins(..))")
    public Object monitorCoinExchange(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime exchangeTime = LocalDateTime.now();
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        double exchangeAmount = (double) args[1];
        String exchangeBank = (String) args[2];

        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "coin_exchange");
        logData.put("user_id", userId);
        logData.put("exchange_amount", exchangeAmount);
        logData.put("bank", exchangeBank);
        logData.put("timestamp", exchangeTime.toString());
        logData.put("hour", exchangeTime.getHour());
        logData.put("day_of_week", exchangeTime.getDayOfWeek().toString());

        try {
            Object result = joinPoint.proceed();
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;
        } catch (Exception e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }
    
}
