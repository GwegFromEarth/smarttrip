package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceRankingServiceTest {

    private final PlaceRankingService placeRankingService =
            new PlaceRankingService();

    @Test
    void shouldReturnEmptyListWhenPlacesAreNull() {
        List<PlaceDto> result =
                placeRankingService.rank(null, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenPlacesAreEmpty() {
        List<PlaceDto> result =
                placeRankingService.rank(List.of(), 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRankPlacesByScore() {
        PlaceDto highlyRated = createPlace(
                "1",
                "Highly Rated",
                1000.0,
                9.0,
                0.8
        );

        PlaceDto lowRated = createPlace(
                "2",
                "Low Rated",
                1000.0,
                5.0,
                0.3
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(lowRated, highlyRated),
                        2
                );

        assertEquals(2, result.size());
        assertEquals("Highly Rated", result.get(0).name());
        assertEquals("Low Rated", result.get(1).name());
    }

    @Test
    void shouldRespectLimit() {
        PlaceDto place1 = createPlace(
                "1",
                "Place 1",
                100.0,
                9.0,
                0.9
        );

        PlaceDto place2 = createPlace(
                "2",
                "Place 2",
                200.0,
                8.0,
                0.8
        );

        PlaceDto place3 = createPlace(
                "3",
                "Place 3",
                300.0,
                7.0,
                0.7
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(place1, place2, place3),
                        2
                );

        assertEquals(2, result.size());
        assertEquals("Place 1", result.get(0).name());
        assertEquals("Place 2", result.get(1).name());
    }

    @Test
    void shouldPreferHigherRating() {
        PlaceDto highRating = createPlace(
                "1",
                "High Rating",
                1000.0,
                10.0,
                0.5
        );

        PlaceDto lowRating = createPlace(
                "2",
                "Low Rating",
                1000.0,
                5.0,
                0.5
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(lowRating, highRating),
                        2
                );

        assertEquals("High Rating", result.get(0).name());
    }

    @Test
    void shouldPreferHigherPopularity() {
        PlaceDto popular = createPlace(
                "1",
                "Popular",
                1000.0,
                8.0,
                1.0
        );

        PlaceDto unpopular = createPlace(
                "2",
                "Unpopular",
                1000.0,
                8.0,
                0.2
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(unpopular, popular),
                        2
                );

        assertEquals("Popular", result.get(0).name());
    }

    @Test
    void shouldPreferCloserPlace() {
        PlaceDto close = createPlace(
                "1",
                "Close",
                100.0,
                8.0,
                0.5
        );

        PlaceDto far = createPlace(
                "2",
                "Far",
                5000.0,
                8.0,
                0.5
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(far, close),
                        2
                );

        assertEquals("Close", result.get(0).name());
    }

    @Test
    void shouldHandleNullRating() {
        PlaceDto withoutRating = createPlace(
                "1",
                "Without Rating",
                100.0,
                null,
                0.8
        );

        PlaceDto withRating = createPlace(
                "2",
                "With Rating",
                100.0,
                8.0,
                0.8
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(withoutRating, withRating),
                        2
                );

        assertEquals("With Rating", result.get(0).name());
    }

    @Test
    void shouldHandleNullPopularity() {
        PlaceDto withoutPopularity = createPlace(
                "1",
                "Without Popularity",
                100.0,
                8.0,
                null
        );

        PlaceDto withPopularity = createPlace(
                "2",
                "With Popularity",
                100.0,
                8.0,
                0.8
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(withoutPopularity, withPopularity),
                        2
                );

        assertEquals("With Popularity", result.get(0).name());
    }

    @Test
    void shouldHandleNullDistance() {
        PlaceDto withoutDistance = createPlace(
                "1",
                "Without Distance",
                null,
                8.0,
                0.5
        );

        PlaceDto withDistance = createPlace(
                "2",
                "With Distance",
                1000.0,
                8.0,
                0.5
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(withoutDistance, withDistance),
                        2
                );

        assertEquals("With Distance", result.get(0).name());
    }

    @Test
    void shouldClampRatingAboveTen() {
        PlaceDto ratingTen = createPlace(
                "1",
                "Rating 10",
                1000.0,
                10.0,
                0.5
        );

        PlaceDto ratingAboveTen = createPlace(
                "2",
                "Rating Above 10",
                1000.0,
                15.0,
                0.5
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(ratingTen, ratingAboveTen),
                        2
                );

        assertEquals(2, result.size());
    }

    @Test
    void shouldClampPopularityAboveOne() {
        PlaceDto popularityOne = createPlace(
                "1",
                "Popularity 1",
                1000.0,
                8.0,
                1.0
        );

        PlaceDto popularityAboveOne = createPlace(
                "2",
                "Popularity Above 1",
                1000.0,
                8.0,
                2.0
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(popularityOne, popularityAboveOne),
                        2
                );

        assertEquals(2, result.size());
    }

    @Test
    void shouldGiveMaximumDistanceScoreToZeroDistance() {
        PlaceDto zeroDistance = createPlace(
                "1",
                "Zero Distance",
                0.0,
                8.0,
                0.5
        );

        PlaceDto oneKilometer = createPlace(
                "2",
                "One Kilometer",
                1000.0,
                8.0,
                0.5
        );

        List<PlaceDto> result =
                placeRankingService.rank(
                        List.of(oneKilometer, zeroDistance),
                        2
                );

        assertEquals("Zero Distance", result.get(0).name());
    }

    private PlaceDto createPlace(
            String id,
            String name,
            Double distance,
            Double rating,
            Double popularity
    ) {
        return new PlaceDto(
                id,
                name,
                "Test description",
                48.8566,
                2.3522,
                PlaceCategory.TOURIST_ATTRACTION,
                "Test address",
                distance,
                rating,
                popularity
        );
    }

}