package com.smarttrip.api.exception;

public class ItineraryNotFoundException extends RuntimeException {

    public ItineraryNotFoundException(Long tripId) {
        super("Itinerary not found for trip id: " + tripId);
    }
}