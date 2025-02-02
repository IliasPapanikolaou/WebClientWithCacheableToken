package com.ipap.webclientwithcacheabletoken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final WebClient webClient;
    private final AuthService authService;
    @Value("${external.clientId}")
    private String clientId;
    @Value("${external.api.url}")
    private String externalApiUrl;


    @Override
    public Mono<ResponseEntity<String>> postData(Object payload) {
        return authService.getJwtToken().flatMap(token ->
                webClient.post()
                        .uri(externalApiUrl)
                        .header("X-Client-id", clientId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .bodyValue(payload)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, response ->
                                response.bodyToMono(String.class).flatMap(body ->
                                        Mono.error(new RuntimeException("POST call failed: " + body))
                                )
                        )
                        .toEntity(String.class)
                        .timeout(Duration.ofSeconds(5))
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(e ->
                                        e instanceof WebClientRequestException || e instanceof TimeoutException))
        );
    }

    @Override
    public Mono<ResponseEntity<String>> patchData(Object payload) {
        return authService.getJwtToken().flatMap(token ->
                webClient.patch()
                        .uri(externalApiUrl)
                        .header("X-Client-id", clientId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .bodyValue(payload)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, response ->
                                response.bodyToMono(String.class).flatMap(body ->
                                        Mono.error(new RuntimeException("PATCH call failed: " + body))
                                )
                        )
                        .toEntity(String.class)
                        .timeout(Duration.ofSeconds(5))
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(e ->
                                        e instanceof WebClientRequestException || e instanceof TimeoutException))
        );
    }
}
