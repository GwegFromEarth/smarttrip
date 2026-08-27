package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.foursquare.FoursquarePlaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private static final int MIN_RADIUS_METERS = 1;
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 100;
    private static final int SEARCH_LIMIT = 50;

    private final FoursquarePlaceService foursquarePlaceService;
    private final PlaceRankingService placeRankingService;

    public PlaceService(
            FoursquarePlaceService foursquarePlaceService,
            PlaceRankingService placeRankingService
    ) {
        this.foursquarePlaceService = foursquarePlaceService;
        this.placeRankingService = placeRankingService;
    }

    public List<PlaceDto> search(
            double latitude,
            double longitude,
            int radiusMeters,
            PlaceCategory category,
            int limit
    ) {
        validateCoordinates(latitude, longitude);
        validateRadius(radiusMeters);
        validateCategory(category);
        validateLimit(limit);

        var places = foursquarePlaceService.searchPlaces(
                latitude,
                longitude,
                radiusMeters,
                category,
                SEARCH_LIMIT
        );

        return placeRankingService.rank(places, limit);
    }

    public List<PlaceDto> searchByDestination(
            String destination,
            int radius,
            PlaceCategory category,
            int limit
    ) {
        validateDestination(destination);
        validateRadius(radius);
        validateCategory(category);
        validateLimit(limit);

        var places = foursquarePlaceService.searchByDestination(
                destination,
                category,
                SEARCH_LIMIT
        );

        return placeRankingService.rank(places, limit);
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

    private void validateCategory(PlaceCategory category) {
        if (category == null) {
            throw new IllegalArgumentException(
                    "Category must not be null"
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