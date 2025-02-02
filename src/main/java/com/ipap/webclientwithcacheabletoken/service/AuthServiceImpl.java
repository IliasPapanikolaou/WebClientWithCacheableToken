package com.ipap.webclientwithcacheabletoken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.jwtDuration}")
    private Long cacheDuration;
    @Value("${auth.server.url}")
    private String authServerUrl;

    private final WebClient webClient;

    // A cached Mono that will reuse the JWT token for 6 hours
    private Mono<String> cachedToken;

    @Override
    public Mono<String> getJwtToken() {
        if (cachedToken == null) {
            cachedToken = createJwtToken().cache(Duration.ofMinutes(cacheDuration));
        }
        return cachedToken;
    }

    private Mono<String> createJwtToken() {
        return webClient.get()
                .uri(authServerUrl)
                .header("X-Client-id", clientId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body ->
                                        Mono.error(new RuntimeException("Error fetching JWT: " + body))
                                )
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(e -> e instanceof WebClientException || e instanceof TimeoutException));
    }
}
