package com.example.claim_service.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "AuthService")
public class AuthServiceConsumerPactTest {

    @Pact(consumer = "ClaimService")
    public V4Pact createPact(PactBuilder builder) {
        return builder
                .usingLegacyDsl()
                .given("User exists")
                .uponReceiving("A request for user profile")
                .path("/api/v1/auth/users/00000000-0000-0000-0000-000000000001")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("{\"userId\": \"00000000-0000-0000-0000-000000000001\", \"fullName\": \"John Doe\", \"email\": \"john@example.com\", \"role\": \"CUSTOMER\"}")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void testAuthServiceContract(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/auth/users/00000000-0000-0000-0000-000000000001";
        
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals("John Doe", response.getBody().get("fullName"));
    }
}
