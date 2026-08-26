package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.geoapify.GeoapifyClient;
import com.smarttrip.api.integration.geoapify.GeoapifyPlaceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private static final int MIN_RADIUS_METERS = 1;
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 100;

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
        validateCoordinates(latitude, longitude);
        validateRadius(radiusMeters);
        validateCategory(category);
        validateLimit(limit);

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
                .map(feature -> geoapifyPlaceMapper.toPlaceDto(
                        feature,
                        category
                ))
                .toList();
    }

    public List<PlaceDto> searchByDestination(
            String destination,
            int radius,
            String category,
            int limit
    ) {
        validateDestination(destination);
        validateRadius(radius);
        validateCategory(category);
        validateLimit(limit);

        var location = geocodingService.geocode(destination);

        return search(
                location.lat(),
                location.lon(),
                radius,
                category,
                limit
        );
    }

    public List<PlaceDto> searchByDestination(
            String destination,
            int radius,
            PlaceCategory category,
            int limit
    ) {
        if (category == null) {
            throw new IllegalArgumentException(
                    "Category must not be null"
            );
        }

        return searchByDestination(
                destination,
                radius,
                category.geoapifyCategory(),
                limit
        );
    }

    private void validateCoordinates(
            double latitude,
            double longitude
    ) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90"
            );
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180"
            );
        }
    }

    private void validateRadius(int radiusMeters) {
        if (radiusMeters < MIN_RADIUS_METERS) {
            throw new IllegalArgumentException(
                    "Radius must be at least "
                            + MIN_RADIUS_METERS
                            + " meter"
            );
        }
    }

    private void validateLimit(int limit) {
        if (limit < MIN_LIMIT || limit > MAX_LIMIT) {
            throw new IllegalArgumentException(
                    "Limit must be between "
                            + MIN_LIMIT
                            + " and "
                            + MAX_LIMIT
            );
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category must not be blank"
            );
        }
    }

    private void validateDestination(String destination) {
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException(
                    "Destination must not be blank"
            );
        }
    }
}