package com.smarttrip.api.integration.foursquare;

public class FoursquareApiException extends RuntimeException {

    private final int statusCode;

    public FoursquareApiException(
            int statusCode,
            String message
    ) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}