package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoursquarePlaceService {

    private static final int MAX_SEARCH_LIMIT = 50;

    /*
     * Foursquare category:
     * Landmarks and Outdoors
     */
    private static final String TOURIST_ATTRACTION_CATEGORY_ID =
            "4bf58dd8d48988d181941735";

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
        String categoryIds = toFoursquareCategoryIds(category);

        FoursquareResponse response = foursquareClient.search(
                latitude,
                longitude,
                radiusMeters,
                query,
                categoryIds,
                Math.min(limit, MAX_SEARCH_LIMIT)
        );

        return mapResults(response, category);
    }

    public List<PlaceDto> searchByDestination(
            String destination,
            int radius,
            PlaceCategory category,
            int limit
    ) {
        String query = toFoursquareQuery(category);
        String categoryIds = toFoursquareCategoryIds(category);

        FoursquareResponse response =
                foursquareClient.searchByDestination(
                        destination,
                        query,
                        categoryIds,
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
            case TOURIST_ATTRACTION -> null;
            case MUSEUM -> "museum";
            case RESTAURANT -> "restaurant";
            case CAFE -> "cafe";
            case PARK -> "park";
        };
    }

    private String toFoursquareCategoryIds(
            PlaceCategory category
    ) {

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category must not be null"
            );
        }

        return switch (category) {
            case TOURIST_ATTRACTION ->
                    TOURIST_ATTRACTION_CATEGORY_ID;

            case MUSEUM,
                 RESTAURANT,
                 CAFE,
                 PARK ->
                    null;
        };
    }
}