package com.picktartup.coinservice.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CoinLoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 모든 컨트롤러 API 호출 로깅
    @Around("execution(* com.picktartup.coinservice.controller..*.*(..))")
    public Object logAllApiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        logData.put("request_id", UUID.randomUUID().toString());
        logData.put("http_method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("api_path", request.getRequestURI().replaceAll("/\\d+", "/{id}")); // 숫자를 {id}로 대체
        logData.put("client_ip", request.getRemoteAddr());

        // API 메타정보
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logData.put("api_name", signature.getDeclaringType().getSimpleName() + "." + signature.getName());

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("success", true);
            logData.put("http_status", 200); // 성공 상태 코드
            log.info(objectMapper.writeValueAsString(logData));
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("success", false);
            logData.put("http_status", 500); // 실패 상태 코드
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }

    // 코인 구매 모니터링
    @Around("execution(* com.picktartup.coinservice.service.CoinServiceImpl.purchaseCoins(..))")
    public Object monitorCoinPurchase(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorServiceCall(joinPoint, "coin_purchase");
    }

    // 코인 현금화 모니터링
    @Around("execution(* com.picktartup.coinservice.service.CoinServiceImpl.exchangeCoins(..))")
    public Object monitorCoinExchange(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorServiceCall(joinPoint, "coin_exchange");
    }

    // 공통 서비스 호출 모니터링
    private Object monitorServiceCall(ProceedingJoinPoint joinPoint, String eventType) throws Throwable {
        long startTime = System.currentTimeMillis();
        LocalDateTime eventTime = LocalDateTime.now();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Object[] args = joinPoint.getArgs();
        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", eventType);
        logData.put("timestamp", eventTime.format(DateTimeFormatter.ISO_DATE_TIME));
        logData.put("hour", eventTime.getHour());
        logData.put("day_of_week", eventTime.getDayOfWeek().toString());

        // API 정보
        logData.put("http_method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("api_path", request.getRequestURI().replaceAll("/\\d+", "/{id}")); // 숫자를 {id}로 대체
        logData.put("client_ip", request.getRemoteAddr());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logData.put("api_name", signature.getDeclaringType().getSimpleName() + "." + signature.getName());
        logData.put("request_id", UUID.randomUUID().toString());

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("success", true);
            logData.put("http_status", 200); // 성공 상태 코드
            log.info(objectMapper.writeValueAsString(logData));
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("success", false);
            logData.put("http_status", 500); // 실패 상태 코드
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }
}
