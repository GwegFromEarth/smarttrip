package com.smarttrip.api.service;

import com.smarttrip.api.integration.geoapify.GeoapifyGeocodingClient;
import com.smarttrip.api.integration.geoapify.GeoapifyGeocodingResult;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private final GeoapifyGeocodingClient geocodingClient;

    public GeocodingService(GeoapifyGeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    public GeoapifyGeocodingResult geocode(String destination) {

        var response = geocodingClient.searchCity(destination);

        if (response.results() == null || response.results().isEmpty()) {
            throw new IllegalArgumentException(
                    "Destination introuvable : " + destination
            );
        }

        return response.results().get(0);
    }
}