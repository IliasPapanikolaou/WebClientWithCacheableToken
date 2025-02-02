package com.ipap.webclientwithcacheabletoken.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MockExternalServiceController {

    @PostMapping
    public Mono<ResponseEntity<String>> postOnExternalService(
            @RequestHeader("X-Client-id") String clientId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody String payload) {
        log.info("External service handling request from ClientId: {} and auth header: {} with payload: {}",
                clientId, authorization, payload);
        return Mono.just(ResponseEntity.ok(payload));
    }
}
