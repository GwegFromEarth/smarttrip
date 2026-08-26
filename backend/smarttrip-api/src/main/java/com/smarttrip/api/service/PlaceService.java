package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.geoapify.GeoapifyClient;
import com.smarttrip.api.integration.geoapify.GeoapifyPlaceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final GeoapifyClient geoapifyClient;
    private final GeocodingService geocodingService;
    private final GeoapifyPlaceMapper geoapifyPlaceMapper;

    public PlaceService(
            GeoapifyClient geoapifyClient,
            GeocodingService geocodingService,
            GeoapifyPlaceMapper geoapifyPlaceMapper
    ) {
        this.geoapifyClient = geoapifyClient;
        this.geocodingService = geocodingService;
        this.geoapifyPlaceMapper = geoapifyPlaceMapper;
    }

    public List<PlaceDto> search(
            double latitude,
            double longitude,
            int radiusMeters,
            String category,
            int limit
    ) {
        var response = geoapifyClient.search(
                latitude,
                longitude,
                radiusMeters,
                category,
                limit
        );

        if (response == null || response.features() == null) {
            return List.of();
        }

        return response.features().stream()
                .map(geoapifyPlaceMapper::toPlaceDto)
                .toList();
    }

    public List<PlaceDto> searchByDestination(
            String destination,
            int radius,
            String category,
            int limit
    ) {
        var location = geocodingService.geocode(destination);

        return search(
                location.lat(),
                location.lon(),
                radius,
                category,
                limit
        );
    }
}