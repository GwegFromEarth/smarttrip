package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoursquarePlaceService {

    private static final int MAX_SEARCH_LIMIT = 50;

    private final FoursquareClient foursquareClient;
    private final FoursquarePlaceMapper mapper;

    public FoursquarePlaceService(
            FoursquareClient foursquareClient,
            FoursquarePlaceMapper mapper
    ) {
        this.foursquareClient = foursquareClient;
        this.mapper = mapper;
    }

    public List<PlaceDto> searchPlaces(
            double latitude,
            double longitude,
            int radiusMeters,
            PlaceCategory category,
            int limit
    ) {
        String query = toFoursquareQuery(category);

        FoursquareResponse response = foursquareClient.search(
                latitude,
                longitude,
                radiusMeters,
                query,
                Math.min(limit, MAX_SEARCH_LIMIT)
        );

        return mapResults(response, category);
    }

    public List<PlaceDto> searchByDestination(
            String destination,
            PlaceCategory category,
            int limit
    ) {
        String query = toFoursquareQuery(category);

        FoursquareResponse response =
                foursquareClient.searchByDestination(
                        destination,
                        query,
                        Math.min(limit, MAX_SEARCH_LIMIT)
                );

        return mapResults(response, category);
    }

    private List<PlaceDto> mapResults(
            FoursquareResponse response,
            PlaceCategory category
    ) {
        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results()
                .stream()
                .map(place -> mapper.toPlaceDto(place, category))
                .toList();
    }

    private String toFoursquareQuery(PlaceCategory category) {
        if (category == null) {
            throw new IllegalArgumentException(
                    "Category must not be null"
            );
        }

        return switch (category) {
            case TOURIST_ATTRACTION -> "tourist attraction";
            case MUSEUM -> "museum";
            case RESTAURANT -> "restaurant";
            case CAFE -> "cafe";
            case PARK -> "park";
        };
    }
}