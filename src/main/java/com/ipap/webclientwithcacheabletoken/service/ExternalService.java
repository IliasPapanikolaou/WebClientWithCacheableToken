package com.ipap.webclientwithcacheabletoken.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ExternalService {
    /**
     * Sends a POST request to the external API.
     *
     * @param payload the request payload
     * @return a reactive Mono containing the API response
     */
    Mono<ResponseEntity<String>> postData(Object payload);

    /**
     * Sends a PATCH request to the external API.
     *
     * @param payload the request payload
     * @return a reactive Mono containing the API response
     */
    Mono<ResponseEntity<String>> patchData(Object payload);
}
