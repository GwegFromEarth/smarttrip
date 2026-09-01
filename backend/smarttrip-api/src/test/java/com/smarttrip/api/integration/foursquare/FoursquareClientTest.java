package com.smarttrip.api.integration.foursquare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.test.MockServerRestClientCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FoursquareClientTest {

    private FoursquareClient foursquareClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockServerRestClientCustomizer customizer =
                new MockServerRestClientCustomizer();

        customizer.customize(restClientBuilder);

        mockServer = customizer.getServer();

        foursquareClient = new FoursquareClient(
                restClientBuilder,
                "test-api-key"
        );
    }

    @Test
    void shouldFindPlacesInRome() {

        String jsonResponse = """
                {
                  "results": [
                    {
                      "fsq_place_id": "test-colosseum-id",
                      "name": "Colosseum",
                      "latitude": 41.8902,
                      "longitude": 12.4922,
                      "geocodes": {
                        "main": {
                          "latitude": 41.8902,
                          "longitude": 12.4922
                        }
                      },
                      "location": {
                        "formatted_address": "Piazza del Colosseo, Rome, Italy"
                      },
                      "categories": [],
                      "distance": 100,
                      "rating": 9.0,
                      "popularity": 0.95
                    }
                  ]
                }
                """;

        mockServer.expect(
                        requestTo(
                                containsString("/places/search")
                        )
                )
                .andExpect(method(GET))
                .andExpect(
                        queryParam(
                                "ll",
                                "41.8903,12.4922"
                        )
                )
                .andExpect(
                        queryParam(
                                "radius",
                                "5000"
                        )
                )
                .andExpect(
                        queryParam(
                                "limit",
                                "10"
                        )
                )
                .andExpect(
                        queryParam(
                                "query",
                                "Colosseum"
                        )
                )
                .andExpect(
                        queryParam(
                                "fields",
                                "fsq_place_id,name,geocodes,location,categories,distance,rating,popularity"
                        )
                )
                .andExpect(
                        header(
                                HttpHeaders.AUTHORIZATION,
                                startsWith("Bearer ")
                        )
                )
                .andExpect(
                        header(
                                "X-Places-Api-Version",
                                "2025-06-17"
                        )
                )
                .andRespond(
                        withSuccess(
                                jsonResponse,
                                MediaType.APPLICATION_JSON
                        )
                );

        FoursquareResponse response =
                foursquareClient.search(
                        41.8903,
                        12.4922,
                        5000,
                        "Colosseum",
                        null,
                        10
                );

        assertNotNull(response);
        assertNotNull(response.results());
        assertFalse(response.results().isEmpty());
        assertNotNull(response.results().get(0));

        assertNotNull(response.results().get(0).name());

        mockServer.verify();
    }

    @Test
    void shouldFindPlacesByDestination() {

        String jsonResponse = """
                {
                  "results": [
                    {
                      "fsq_place_id": "test-colosseum-id",
                      "name": "Colosseum",
                      "latitude": 41.8902,
                      "longitude": 12.4922,
                      "geocodes": {
                        "main": {
                          "latitude": 41.8902,
                          "longitude": 12.4922
                        }
                      },
                      "location": {
                        "formatted_address": "Piazza del Colosseo, Rome, Italy"
                      },
                      "categories": [],
                      "distance": 100,
                      "rating": 9.0,
                      "popularity": 0.95
                    }
                  ]
                }
                """;

        mockServer.expect(
                        requestTo(
                                containsString("/places/search")
                        )
                )
                .andExpect(method(GET))
                .andExpect(
                        queryParam(
                                "near",
                                "Rome"
                        )
                )
                .andExpect(
                        queryParam(
                                "limit",
                                "10"
                        )
                )
                .andExpect(
                        queryParam(
                                "query",
                                "Colosseum"
                        )
                )
                .andExpect(
                        queryParam(
                                "fields",
                                "fsq_place_id,name,geocodes,location,categories,distance,rating,popularity"
                        )
                )
                .andExpect(
                        header(
                                HttpHeaders.AUTHORIZATION,
                                startsWith("Bearer ")
                        )
                )
                .andExpect(
                        header(
                                "X-Places-Api-Version",
                                "2025-06-17"
                        )
                )
                .andRespond(
                        withSuccess(
                                jsonResponse,
                                MediaType.APPLICATION_JSON
                        )
                );

        FoursquareResponse response =
                foursquareClient.searchByDestination(
                        "Rome",
                        "Colosseum",
                        null,
                        10
                );

        assertNotNull(response);
        assertNotNull(response.results());
        assertFalse(response.results().isEmpty());
        assertNotNull(response.results().get(0));

        assertNotNull(response.results().get(0).name());

        mockServer.verify();
    }
}