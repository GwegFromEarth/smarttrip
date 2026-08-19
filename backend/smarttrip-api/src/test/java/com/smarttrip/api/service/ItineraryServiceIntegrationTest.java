package com.smarttrip.api.service;

import com.smarttrip.api.dto.Itinerary;
import com.smarttrip.api.model.Trip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItineraryServiceIntegrationTest {

    @Autowired
    private ItineraryService itineraryService;

    @Test
    void shouldGenerateItineraryFromTrip() {
        Trip trip = new Trip();

        trip.setDestination("Rome");
        trip.setStartDate(LocalDate.of(2026, 9, 12));
        trip.setEndDate(LocalDate.of(2026, 9, 17));
        trip.setTravelers(2);
        trip.setPreferences("culture, histoire, gastronomie");

        Itinerary itinerary = itineraryService.generateItinerary(trip);

        assertThat(itinerary).isNotNull();
        assertThat(itinerary.destination()).isNotBlank();
        assertThat(itinerary.days()).isNotEmpty();

        System.out.println("=== ITINERARY GENERATED ===");
        System.out.println(itinerary);
    }
}