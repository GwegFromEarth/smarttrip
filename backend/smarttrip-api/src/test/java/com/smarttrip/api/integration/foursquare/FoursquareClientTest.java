package com.smarttrip.api.integration.foursquare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FoursquareClientTest {

    @Autowired
    private FoursquareClient foursquareClient;

    @Test
    void shouldFindPlacesInRome() {

        FoursquareResponse response = foursquareClient.search(
                41.8903,
                12.4922,
                5000,
                "Colosseum",
                10
        );

        assertNotNull(response);
        assertNotNull(response.results());
        assertFalse(response.results().isEmpty());
    }

    @Test
    void shouldFindPlacesByDestination() {

        FoursquareResponse response =
                foursquareClient.searchByDestination(
                        "Rome",
                        "Colosseum",
                        10
                );

        assertNotNull(response);
        assertNotNull(response.results());
        assertFalse(response.results().isEmpty());
    }
}