package com.ipap.webclientwithcacheabletoken.service;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.Headers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


class ExternalServiceImplTest {

    private static MockWebServer mockBackEnd;
    private static ExternalService externalService;
    private AuthService authService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void testPostData_Success() {

        // Arrange: Set up a fake JWT token from the AuthService
        String fakeToken = "fakeToken";
        authService = Mockito.mock(AuthService.class);
        when(authService.getJwtToken()).thenReturn(Mono.just(fakeToken));

        String fakeResponse = "post-success";
        Headers headers = Headers.of(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
        mockBackEnd.enqueue(new MockResponse(200, headers, fakeResponse));

        String baseUrl = String.format("http://localhost:%d/", mockBackEnd.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        externalService = new ExternalServiceImpl(webClient, authService);

        // Act
        Mono<ResponseEntity<String>> responseMono = externalService.postData(new TestPayload("data"));

        // Assert using StepVerifier
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(fakeResponse);
                }).verifyComplete();
    }

    @Test
    void testPatchData_success() {
        // Arrange: Set up a fake JWT token from the AuthService
        String fakeToken = "fake-jwt-token";
        authService = Mockito.mock(AuthService.class);
        when(authService.getJwtToken()).thenReturn(Mono.just(fakeToken));

        String fakeResponse = "patch-success";
        Headers headers = Headers.of(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
        mockBackEnd.enqueue(new MockResponse(200, headers, fakeResponse));

        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        externalService = new ExternalServiceImpl(webClient, authService);

        // Act
        Mono<ResponseEntity<String>> responseMono = externalService.patchData(new TestPayload("data"));

        // Assert using StepVerifier
        StepVerifier.create(responseMono)
                .assertNext(response ->
                        assertThat(response.getBody()).isEqualTo(fakeResponse))
                .verifyComplete();
    }

    public record TestPayload(String data) {
    }
}