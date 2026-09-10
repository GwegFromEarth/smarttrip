package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.service.PlaceRankingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FoursquarePlaceService {

    private static final int MAX_SEARCH_LIMIT = 20;

    /*
     * Foursquare category used for tourist attractions:
     * - Monuments
     */
    private static final String MONUMENTS_CATEGORY_ID =
            "4bf58dd8d48988d12d941735";

    /*
     * Names that usually correspond to secondary/sub-sites
     * rather than major tourist attractions.
     */
    private static final Set<String> EXCLUDED_TOURIST_ATTRACTIONS = Set.of(
            "Emperor Seats",
            "House of the Vestal Virgins (Casa delle Vestali)",
            "Tomba di Vittorio Emanuele II",
            "Tomba del Beato Giovanni Paolo II"
    );

    private final FoursquareClient foursquareClient;
    private final FoursquarePlaceMapper mapper;
    private final PlaceRankingService placeRankingService;

    public FoursquarePlaceService(
            FoursquareClient foursquareClient,
            FoursquarePlaceMapper mapper,
            PlaceRankingService placeRankingService
    ) {
        this.foursquareClient = foursquareClient;
        this.mapper = mapper;
        this.placeRankingService = placeRankingService;
    }

    @Cacheable(
            cacheNames = "foursquarePlaces",
            key = "'coords:' + T(java.lang.String).format('%.4f', #latitude) + ':' + " +
                    "T(java.lang.String).format('%.4f', #longitude) + ':' + " +
                    "#radiusMeters + ':' + #category + ':' + #limit"
    )
    public List<PlaceDto> searchPlaces(
            double latitude,
            double longitude,
            int radiusMeters,
            PlaceCategory category,
            int limit
    ) {
        int searchLimit = Math.min(limit, MAX_SEARCH_LIMIT);

        String query = toFoursquareQuery(category);
        String categoryIds = toFoursquareCategoryIds(category);
        String sort = toFoursquareSort(category);

        FoursquareResponse response = foursquareClient.search(
                latitude,
                longitude,
                radiusMeters,
                query,
                categoryIds,
                sort,
                searchLimit
        );

        return mapResults(response, category);
    }

    @Cacheable(
            cacheNames = "foursquarePlaces",
            key = "'destination:' + #destination.toLowerCase() + ':' + #category + ':' + #limit"
    )
    public List<PlaceDto> searchByDestination(
            String destination,
            PlaceCategory category,
            int limit
    ) {
        int requestedLimit = Math.min(limit, MAX_SEARCH_LIMIT);

        /*
         * We request the maximum number of results from Foursquare
         * so that we have enough candidates after filtering.
         */
        FoursquareResponse response =
                foursquareClient.searchByDestination(
                        destination,
                        toFoursquareQuery(category),
                        toFoursquareCategoryIds(category),
                        MAX_SEARCH_LIMIT
                );

        List<PlaceDto> candidates = mapResults(response, category)
                .stream()
                .filter(place -> !isExcludedTouristAttraction(place))
                .toList();

        return placeRankingService.rank(candidates, requestedLimit);
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

    private boolean isExcludedTouristAttraction(PlaceDto place) {

        if (place == null || place.name() == null) {
            return false;
        }

        if (place.category() != PlaceCategory.TOURIST_ATTRACTION) {
            return false;
        }

        return EXCLUDED_TOURIST_ATTRACTIONS.contains(
                place.name().trim()
        );
    }

    private String toFoursquareSort(PlaceCategory category) {

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category must not be null"
            );
        }

        return switch (category) {
            case TOURIST_ATTRACTION -> "POPULARITY";
            case MUSEUM -> "RATING";
            case RESTAURANT -> "RATING";
            case CAFE -> "RATING";
            case PARK -> "POPULARITY";
        };
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
                    MONUMENTS_CATEGORY_ID;

            case MUSEUM,
                 RESTAURANT,
                 CAFE,
                 PARK ->
                    null;
        };
    }
}