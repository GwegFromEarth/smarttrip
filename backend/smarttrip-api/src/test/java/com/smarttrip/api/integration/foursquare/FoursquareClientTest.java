package com.smarttrip.api.integration.foursquare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(FoursquareClient.class)
class FoursquareClientTest {

    @Autowired
    private FoursquareClient foursquareClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @TestConfiguration
    static class TestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("foursquarePlaces");
        }
    }

    @Test
    void shouldSearchPlacesByCoordinates() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("ll", "48.8566,2.3522"))
                .andExpect(queryParam("radius", "1000"))
                .andExpect(queryParam("limit", "10"))
                .andExpect(queryParam(
                        "fields",
                        "fsq_place_id,name,geocodes,location,categories,distance,rating,popularity"
                ))
                .andExpect(queryParam("sort", "RATING"))
                .andExpect(queryParam("query", "museum"))
                .andExpect(queryParam(
                        "fsq_category_ids",
                        "4bf58dd8d48988d181941735"
                ))
                .andRespond(withSuccess(
                        """
                        {
                          "results": [
                            {
                              "fsq_place_id": "test-louvre-id",
                              "name": "Louvre Museum",
                              "geocodes": {
                                "main": {
                                  "latitude": 48.8606,
                                  "longitude": 2.3376
                                }
                              },
                              "location": {
                                "address": "Rue de Rivoli",
                                "locality": "Paris",
                                "region": "Île-de-France",
                                "postcode": "75001",
                                "country": "France"
                              },
                              "categories": [],
                              "distance": 500,
                              "rating": 9.2,
                              "popularity": 0.95
                            }
                          ]
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        FoursquareResponse response = foursquareClient.search(
                48.8566,
                2.3522,
                1000,
                "museum",
                "4bf58dd8d48988d181941735",
                "RATING",
                10
        );

        assertThat(response).isNotNull();
        assertThat(response.results()).hasSize(1);

        assertThat(response.results().getFirst().name())
                .isEqualTo("Louvre Museum");

        assertThat(response.results().getFirst().rating())
                .isEqualTo(9.2);

        mockServer.verify();
    }

    @Test
    void shouldSearchPlacesWithoutOptionalParameters() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("ll", "41.8902,12.4922"))
                .andExpect(queryParam("radius", "1000"))
                .andExpect(queryParam("limit", "10"))
                .andExpect(queryParam(
                        "fields",
                        "fsq_place_id,name,geocodes,location,categories,distance,rating,popularity"
                ))
                .andExpect(queryParam("sort", "POPULARITY"))
                .andRespond(withSuccess(
                        """
                        {
                          "results": []
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        FoursquareResponse response = foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                null,
                null,
                "POPULARITY",
                10
        );

        assertThat(response).isNotNull();
        assertThat(response.results()).isEmpty();

        mockServer.verify();
    }

    @Test
    void shouldSearchPlacesByDestination() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("near", "Rome"))
                .andExpect(queryParam("limit", "10"))
                .andExpect(queryParam(
                        "fields",
                        "fsq_place_id,name,geocodes,location,categories,distance,rating,popularity"
                ))
                .andExpect(queryParam("query", "museum"))
                .andExpect(queryParam(
                        "fsq_category_ids",
                        "4bf58dd8d48988d181941735"
                ))
                .andRespond(withSuccess(
                        """
                        {
                          "results": [
                            {
                              "fsq_place_id": "test-capitoline-id",
                              "name": "Capitoline Museums",
                              "geocodes": {
                                "main": {
                                  "latitude": 48.8606,
                                  "longitude": 2.3376
                                }
                              },
                              "location": {
                                "address": "Piazza del Campidoglio",
                                "locality": "Rome",
                                "region": "Lazio",
                                "postcode": "00186",
                                "country": "Italy"
                              },
                              "categories": [],
                              "distance": 800,
                              "rating": 8.7,
                              "popularity": 0.9
                            }
                          ]
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        FoursquareResponse response =
                foursquareClient.searchByDestination(
                        "Rome",
                        "museum",
                        "4bf58dd8d48988d181941735",
                        10
                );

        assertThat(response).isNotNull();
        assertThat(response.results()).hasSize(1);

        assertThat(response.results().getFirst().name())
                .isEqualTo("Capitoline Museums");

        mockServer.verify();
    }

    @Test
    void shouldThrowFoursquareApiExceptionWhenUnauthorized() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.UNAUTHORIZED)
                );

        FoursquareApiException exception = assertThrows(
                FoursquareApiException.class,
                () -> foursquareClient.search(
                        48.8566,
                        2.3522,
                        1000,
                        "museum",
                        null,
                        10
                )
        );

        assertThat(exception.getStatusCode())
                .isEqualTo(401);

        assertThat(exception.getMessage())
                .isEqualTo("Foursquare authentication failed");

        mockServer.verify();
    }

    @Test
    void shouldThrowFoursquareApiExceptionWhenRateLimitExceeded() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.TOO_MANY_REQUESTS)
                );

        FoursquareApiException exception = assertThrows(
                FoursquareApiException.class,
                () -> foursquareClient.search(
                        48.8566,
                        2.3522,
                        1000,
                        "museum",
                        null,
                        10
                )
        );

        assertThat(exception.getStatusCode())
                .isEqualTo(429);

        assertThat(exception.getMessage())
                .isEqualTo("Foursquare rate limit exceeded");

        mockServer.verify();
    }

    @Test
    void shouldThrowFoursquareApiExceptionWhenBadRequest() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                );

        FoursquareApiException exception = assertThrows(
                FoursquareApiException.class,
                () -> foursquareClient.search(
                        48.8566,
                        2.3522,
                        1000,
                        "museum",
                        null,
                        10
                )
        );

        assertThat(exception.getStatusCode())
                .isEqualTo(400);

        assertThat(exception.getMessage())
                .isEqualTo("Foursquare request failed");

        mockServer.verify();
    }

    @Test
    void shouldThrowFoursquareApiExceptionWhenFoursquareIsUnavailable() {

        mockServer.expect(request -> {
                    assertThat(request.getURI().getPath())
                            .isEqualTo("/places/search");
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                );

        FoursquareApiException exception = assertThrows(
                FoursquareApiException.class,
                () -> foursquareClient.search(
                        48.8566,
                        2.3522,
                        1000,
                        "museum",
                        null,
                        10
                )
        );

        assertThat(exception.getStatusCode())
                .isEqualTo(500);

        assertThat(exception.getMessage())
                .isEqualTo("Foursquare service unavailable");

        mockServer.verify();
    }
}