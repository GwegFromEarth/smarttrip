package com.smarttrip.api.integration.foursquare;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FoursquareClient {

    private static final String API_VERSION = "2025-06-17";

    private static final String PLACE_FIELDS =
            "fsq_place_id,name,geocodes,location,categories,distance,rating,popularity";

    private final RestClient restClient;

    public FoursquareClient(
            RestClient.Builder restClientBuilder,
            @Value("${foursquare.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://places-api.foursquare.com")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        "X-Places-Api-Version",
                        API_VERSION
                )
                .build();
    }

    public FoursquareResponse search(
            double latitude,
            double longitude,
            int radiusMeters,
            String query,
            String categoryIds,
            String sort,
            int limit
    ) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/places/search")
                            .queryParam(
                                    "ll",
                                    "%s,%s".formatted(latitude, longitude)
                            )
                            .queryParam("radius", radiusMeters)
                            .queryParam("limit", limit)
                            .queryParam("fields", PLACE_FIELDS)
                            .queryParam("sort", sort);

                    if (query != null && !query.isBlank()) {
                        builder.queryParam("query", query);
                    }

                    if (categoryIds != null && !categoryIds.isBlank()) {
                        builder.queryParam(
                                "fsq_category_ids",
                                categoryIds
                        );
                    }

                    return builder.build();
                })
                .retrieve()
                .onStatus(
                        status -> status.value() == 401,
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    401,
                                    "Foursquare authentication failed"
                            );
                        }
                )
                .onStatus(
                        status -> status.value() == 429,
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    429,
                                    "Foursquare rate limit exceeded"
                            );
                        }
                )
                .onStatus(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    response.getStatusCode().value(),
                                    "Foursquare request failed"
                            );
                        }
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    response.getStatusCode().value(),
                                    "Foursquare service unavailable"
                            );
                        }
                )
                .body(FoursquareResponse.class);
    }

    public FoursquareResponse search(
            double latitude,
            double longitude,
            int radiusMeters,
            String query,
            String categoryIds,
            int limit
    ) {
        return search(
                latitude,
                longitude,
                radiusMeters,
                query,
                categoryIds,
                null,
                limit
        );
    }

    public FoursquareResponse searchByDestination(
            String destination,
            String query,
            String categoryIds,
            int limit
    ) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/places/search")
                            .queryParam("near", destination)
                            .queryParam("limit", limit)
                            .queryParam("fields", PLACE_FIELDS);

                    if (query != null && !query.isBlank()) {
                        builder.queryParam("query", query);
                    }

                    if (categoryIds != null && !categoryIds.isBlank()) {
                        builder.queryParam(
                                "fsq_category_ids",
                                categoryIds
                        );
                    }

                    return builder.build();
                })
                .retrieve().onStatus(
                        status -> status.value() == 401,
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    401,
                                    "Foursquare authentication failed"
                            );
                        }
                )
                .onStatus(
                        status -> status.value() == 429,
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    429,
                                    "Foursquare rate limit exceeded"
                            );
                        }
                )
                .onStatus(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    response.getStatusCode().value(),
                                    "Foursquare request failed"
                            );
                        }
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        (request, response) -> {
                            throw new FoursquareApiException(
                                    response.getStatusCode().value(),
                                    "Foursquare service unavailable"
                            );
                        }
                )
                .body(FoursquareResponse.class);
    }
}