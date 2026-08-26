package com.smarttrip.api.integration.geoapify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeoapifyGeocodingClient {

    private final RestClient restClient;
    private final String apiKey;

    public GeoapifyGeocodingClient(
            RestClient.Builder restClientBuilder,
            @Value("${geoapify.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.geoapify.com")
                .build();

        this.apiKey = apiKey;
    }

    public GeoapifyGeocodingResponse searchCity(String city) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/geocode/search")
                        .queryParam("text", city)
                        .queryParam("type", "city")
                        .queryParam("limit", 1)
                        .queryParam("format", "json")
                        .queryParam("apiKey", apiKey)
                        .build()
                )
                .retrieve()
                .body(GeoapifyGeocodingResponse.class);
    }
}