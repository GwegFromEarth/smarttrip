package com.smarttrip.api.integration.overpass;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OverpassClient {

    private final RestClient restClient;

    public OverpassClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://overpass-api.de")
                .build();
    }

    public OverpassResponse search(String query) {
        return restClient
                .post()
                .uri("/api/interpreter")
                .contentType(MediaType.TEXT_PLAIN)
                .body(query)
                .retrieve()
                .body(OverpassResponse.class);
    }
}