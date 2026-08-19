package com.smarttrip.api.service;

import com.smarttrip.api.dto.ItineraryDto;
import com.smarttrip.api.model.Trip;
import com.smarttrip.api.repository.ItineraryRepository;
import com.smarttrip.api.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItineraryServiceIntegrationTest {

    @Autowired
    private ItineraryService itineraryService;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Test
    void shouldGenerateAndPersistItineraryFromTrip() {

        Trip trip = new Trip();

        trip.setDestination("Rome");
        trip.setStartDate(LocalDate.of(2026, 9, 12));
        trip.setEndDate(LocalDate.of(2026, 9, 17));
        trip.setTravelers(2);
        trip.setPreferences("culture, histoire, gastronomie");

        Trip savedTrip = tripRepository.save(trip);

        ItineraryDto itinerary =
                itineraryService.generateItinerary(savedTrip.getId());

        var persistedItinerary = itineraryRepository
                .findByTripId(savedTrip.getId())
                .orElseThrow();

        assertThat(itinerary).isNotNull();
        assertThat(itinerary.destination()).isNotBlank();
        assertThat(itinerary.days()).hasSize(2);

        assertThat(persistedItinerary.getDestination())
                .isEqualTo("Rome");

        assertThat(persistedItinerary.getDays())
                .hasSize(2);

        assertThat(persistedItinerary.getDays().get(0).getDayNumber())
                .isEqualTo(1);

        assertThat(persistedItinerary.getDays().get(0).getDate())
                .isEqualTo(LocalDate.of(2026, 9, 12));

        assertThat(persistedItinerary.getDays().get(0).getActivities())
                .hasSize(2);

        assertThat(persistedItinerary.getDays().get(1).getDayNumber())
                .isEqualTo(2);

        assertThat(persistedItinerary.getDays().get(1).getDate())
                .isEqualTo(LocalDate.of(2026, 9, 13));

        assertThat(persistedItinerary.getDays().get(1).getActivities())
                .hasSize(2);

        persistedItinerary.getDays().forEach(day ->
                day.getActivities().forEach(activity -> {
                    assertThat(activity.getTime()).isNotBlank();
                    assertThat(activity.getTitle()).isNotBlank();
                    assertThat(activity.getDescription()).isNotBlank();
                    assertThat(activity.getLocation()).isNotBlank();
                })
        );

        System.out.println("=== ITINERARY GENERATED ===");
        System.out.println(itinerary);
    }
}