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

        System.out.println();
        System.out.println("========== FOURSQUARE RESULTS ==========");

        response.results().forEach(place -> {

            System.out.println();
            System.out.println("Name       : " + place.name());
            System.out.println("ID         : " + place.fsq_id());
            System.out.println("Distance   : " + place.distance() + " m");

            System.out.println("Latitude   : " + place.latitude());
            System.out.println("Longitude  : " + place.longitude());

            System.out.println("Categories : " + place.categories());

            if (place.location() != null) {
                System.out.println(
                        "Address    : "
                                + place.location().address()
                );
                System.out.println(
                        "Locality   : "
                                + place.location().locality()
                );
                System.out.println(
                        "Region     : "
                                + place.location().region()
                );
                System.out.println(
                        "Postcode   : "
                                + place.location().postcode()
                );
                System.out.println(
                        "Country    : "
                                + place.location().country()
                );
            } else {
                System.out.println("Location   : null");
            }
        });

        System.out.println();
        System.out.println("=========================================");
        System.out.println();
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

        System.out.println();
        System.out.println("====== FOURSQUARE DESTINATION RESULTS ======");

        response.results().forEach(place -> {

            System.out.println();
            System.out.println("Name       : " + place.name());
            System.out.println("ID         : " + place.fsq_id());
            System.out.println("Distance   : " + place.distance() + " m");
            System.out.println("Latitude   : " + place.latitude());
            System.out.println("Longitude  : " + place.longitude());
            System.out.println("Categories : " + place.categories());

            if (place.location() != null) {
                System.out.println(
                        "Address    : "
                                + place.location().address()
                );
                System.out.println(
                        "Locality   : "
                                + place.location().locality()
                );
                System.out.println(
                        "Region     : "
                                + place.location().region()
                );
                System.out.println(
                        "Postcode   : "
                                + place.location().postcode()
                );
                System.out.println(
                        "Country    : "
                                + place.location().country()
                );
            } else {
                System.out.println("Location   : null");
            }
        });

        System.out.println();
        System.out.println("============================================");
        System.out.println();
    }
}