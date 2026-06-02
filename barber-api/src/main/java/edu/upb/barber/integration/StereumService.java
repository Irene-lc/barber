package edu.upb.barber.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class StereumService {

    @Value("${stereum.base-url}")
    private String baseUrl;

    @Value("${stereum.api-key}")
    private String apiKey;

    public String createCharge(StereumCreateChargeRequest request) {
        RestClient restClient = RestClient.builder().build();

        ResponseEntity<String> response = restClient.post()
                .uri(baseUrl + "/api/v1/transactions/create-charge")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("x-api-key", apiKey)
                .body(request)
                .retrieve()
                .toEntity(String.class);

        return response.getBody();
    }
}
