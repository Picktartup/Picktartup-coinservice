package com.picktartup.coinservice.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfig {
    @Value("${service.wallet.url}")
    private String walletServiceUrl;

    @Bean
    public WebClient walletServiceWebClient() {
        return WebClient.builder()
                .baseUrl(walletServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> {
                            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                            return Mono.just(clientRequest);
                        }
                ))
                .build();
    }
}
