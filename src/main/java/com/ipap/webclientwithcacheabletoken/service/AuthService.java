package com.ipap.webclientwithcacheabletoken.service;

import reactor.core.publisher.Mono;

public interface AuthService {
    /**
     * Retrieves a cached JWT token as a reactive Mono.
     * The token is cached for 6 hours.
     */
    Mono<String> getJwtToken();
}
