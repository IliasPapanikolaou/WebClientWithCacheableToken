package com.ipap.webclientwithcacheabletoken.controller;

import com.ipap.webclientwithcacheabletoken.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/get-jwt")
public class MockAuthController {

    private final AuthService authService;

    @GetMapping
    public Mono<String> generateMockJWT(@RequestHeader("X-Client-id") String clientId) {
        log.info("Generating mock JWT for clientId: {}", clientId);
        // Mock JWT
        return Mono.just(Base64Util.encode(UUID.randomUUID().toString().replace("-", "")));
    }
}
