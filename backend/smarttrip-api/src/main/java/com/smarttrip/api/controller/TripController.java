package com.smarttrip.api.controller;

import com.smarttrip.api.dto.CreateTripRequest;
import com.smarttrip.api.dto.TripResponse;
import com.smarttrip.api.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smarttrip.api.dto.ItineraryDto;
import com.smarttrip.api.service.ItineraryService;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final ItineraryService itineraryService;

    public TripController(
            TripService tripService,
            ItineraryService itineraryService) {

        this.tripService = tripService;
        this.itineraryService = itineraryService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody CreateTripRequest request) {

        return ResponseEntity.ok(tripService.createTrip(request));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips() {
        return ResponseEntity.ok(tripService.getTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    @PostMapping("/{id}/itinerary")
    public ResponseEntity<ItineraryDto> generateItinerary(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                itineraryService.generateItinerary(id)
        );
    }
}