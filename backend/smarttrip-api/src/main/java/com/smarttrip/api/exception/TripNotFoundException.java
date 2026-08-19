package com.smarttrip.api.exception;

public class TripNotFoundException extends RuntimeException {

    public TripNotFoundException(Long id) {
        super("Trip not found: " + id);
    }
}