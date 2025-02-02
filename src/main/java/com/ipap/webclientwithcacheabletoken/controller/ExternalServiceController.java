package com.ipap.webclientwithcacheabletoken.controller;

import com.ipap.webclientwithcacheabletoken.service.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ExternalServiceController {

    private final ExternalService externalService;

    @PostMapping
    public Mono<ResponseEntity<String>> postOnExternalService(
            @RequestBody String payload) {
        return externalService.postData(payload);
    }
}
