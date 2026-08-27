package com.smarttrip.api.integration.geoapify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeoapifyClient {

    private final RestClient restClient;
    private final String apiKey;

    public GeoapifyClient(
            RestClient.Builder restClientBuilder,
            @Value("${geoapify.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.geoapify.com")
                .build();

        this.apiKey = apiKey;
    }

    public GeoapifyResponse search(
            double latitude,
            double longitude,
            int radiusMeters,
            String category,
            int limit
    ) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/places")
                        .queryParam("categories", category)
                        .queryParam(
                                "filter",
                                "circle:%s,%s,%d"
                                        .formatted(
                                                longitude,
                                                latitude,
                                                radiusMeters
                                        )
                        )
                        .queryParam(
                                "bias",
                                "proximity:%s,%s"
                                        .formatted(
                                                longitude,
                                                latitude
                                        )
                        )
                        .queryParam("lang", "fr")
                        .queryParam("limit", limit)
                        .queryParam("apiKey", apiKey)
                        .build()
                )
                .retrieve()
                .body(GeoapifyResponse.class);
    }
}