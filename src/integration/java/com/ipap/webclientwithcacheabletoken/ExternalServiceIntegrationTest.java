package com.ipap.webclientwithcacheabletoken;

import com.ipap.webclientwithcacheabletoken.service.ExternalService;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.Headers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ExternalServiceIntegrationTest {

    static MockWebServer mockWebServer;

    // Expose the port to Spring via a system property
    static {
        try {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
            System.setProperty("mock.server.port", String.valueOf(mockWebServer.getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Autowired
    private ExternalService externalService;

    @Test
    void testExternalService_postData() {
        // Arrange:
        // 1. The auth server call returns a token.
        Headers headers = Headers.of(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
        mockWebServer.enqueue(new MockResponse(200, headers, "integration-jwt-token"));
        // 2. The external POST endpoint returns a success message.
        mockWebServer.enqueue(new MockResponse(200, headers, "integration-post-success"));

        // Act: call the POST endpoint using the reactive service
        var responseMono = externalService.postData(new Payload("integration data"));

        // Assert using StepVerifier
        StepVerifier.create(responseMono)
                .assertNext(response ->
                        assertThat(response.getBody()).isEqualTo("integration-post-success")
                )
                .verifyComplete();
    }

    // A simple payload record for testing.
    public record Payload(String data) {}
}
