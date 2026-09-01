package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PlaceRankingService {

    public List<PlaceDto> rank(
            List<PlaceDto> places,
            int limit
    ) {
        if (places == null || places.isEmpty()) {
            return List.of();
        }

        return places.stream()
                .sorted(
                        Comparator.comparingDouble(
                                this::calculateScore
                        ).reversed()
                )
                .limit(limit)
                .toList();
    }

    private double calculateScore(PlaceDto place) {

        double ratingScore = normalizeRating(place.rating());
        double popularityScore = normalizePopularity(place.popularity());
        double distanceScore = calculateDistanceScore(place.distance());

        return
                (ratingScore * 0.40)
                        + (popularityScore * 0.30)
                        + (distanceScore * 0.30);
    }

    private double normalizeRating(Double rating) {

        if (rating == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(
                        rating / 10.0,
                        1.0
                )
        );
    }

    private double normalizePopularity(Double popularity) {

        if (popularity == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(
                        popularity,
                        1.0
                )
        );
    }

    private double calculateDistanceScore(Double distance) {

        if (distance == null) {
            return 0.0;
        }

        /*
         * 0 m  -> 1.0
         * 500 m -> ~0.67
         * 1000 m -> 0.5
         * 2000 m -> ~0.33
         * etc.
         */
        return 1.0 / (1.0 + (distance / 1000.0));
    }
}