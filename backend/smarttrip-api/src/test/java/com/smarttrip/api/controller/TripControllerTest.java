package com.smarttrip.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.smarttrip.api.dto.CreateTripRequest;
import com.smarttrip.api.dto.ItineraryDto;
import com.smarttrip.api.dto.TripResponse;
import com.smarttrip.api.exception.InvalidTripException;
import com.smarttrip.api.exception.ItineraryNotFoundException;
import com.smarttrip.api.exception.TripNotFoundException;
import com.smarttrip.api.service.ItineraryService;
import com.smarttrip.api.service.TripService;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @MockitoBean
    private ItineraryService itineraryService;

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Test
    void createTrip_shouldReturnCreatedTrip() throws Exception {
        TripResponse response = new TripResponse(
                1L,
                "Rome",
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 17),
                2,
                "culture, histoire"
        );

        given(tripService.createTrip(any(CreateTripRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "destination": "Rome",
                                    "startDate": "2026-09-12",
                                    "endDate": "2026-09-17",
                                    "travelers": 2,
                                    "preferences": "culture, histoire"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.destination").value("Rome"))
                .andExpect(jsonPath("$.travelers").value(2));
    }

    @Test
    void createTrip_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "destination": "",
                                    "startDate": null,
                                    "endDate": null,
                                    "travelers": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTrips_shouldReturnTrips() throws Exception {
        TripResponse response = new TripResponse(
                1L,
                "Rome",
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 17),
                2,
                "culture"
        );

        given(tripService.getTrips())
                .willReturn(List.of(response));

        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].destination").value("Rome"));
    }

    @Test
    void getTrip_shouldReturnNotFoundWhenTripDoesNotExist() throws Exception {
        given(tripService.getTrip(999L))
                .willThrow(new TripNotFoundException(999L));

        mockMvc.perform(get("/api/trips/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTrip_shouldReturnBadRequestWhenDatesAreInvalid() throws Exception {
        given(tripService.createTrip(any(CreateTripRequest.class)))
                .willThrow(new InvalidTripException(
                        "Start date must be before or equal to end date"
                ));

        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "destination": "Rome",
                                    "startDate": "2026-09-17",
                                    "endDate": "2026-09-12",
                                    "travelers": 2,
                                    "preferences": "culture"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateItinerary_shouldReturnItinerary() throws Exception {

        ItineraryDto itineraryDto = new ItineraryDto(
                1L,
                "Rome",
                List.of()
        );

        given(itineraryService.generateItinerary(1L))
                .willReturn(itineraryDto);

        mockMvc.perform(post("/api/trips/1/itinerary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.destination").value("Rome"));
    }

    @Test
    void getItinerary_shouldReturnItinerary() throws Exception {

        ItineraryDto itineraryDto = new ItineraryDto(
                1L,
                "Rome",
                List.of()
        );

        given(itineraryService.getItinerary(1L))
                .willReturn(itineraryDto);

        mockMvc.perform(get("/api/trips/1/itinerary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.destination").value("Rome"));
    }

    @Test
    void getItinerary_shouldReturnNotFoundWhenItineraryDoesNotExist() throws Exception {

        given(itineraryService.getItinerary(999L))
                .willThrow(new ItineraryNotFoundException(999L));

        mockMvc.perform(get("/api/trips/999/itinerary"))
                .andExpect(status().isNotFound());
    }
}