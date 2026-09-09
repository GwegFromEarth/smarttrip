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
        double categoryScore = calculateCategoryScore(place);

        return
                (ratingScore * 0.30)
                        + (popularityScore * 0.25)
                        + (distanceScore * 0.25)
                        + (categoryScore * 0.20);
    }

    private double calculateCategoryScore(PlaceDto place) {

        if (place.categories() == null
                || place.categories().isEmpty()) {
            return 0.0;
        }

        double score = 0.0;

        for (String category : place.categories()) {

            if (category == null) {
                continue;
            }

            String normalizedCategory =
                    category.toLowerCase();

            if (normalizedCategory.contains(
                    "historic and protected site"
            )) {
                score = Math.max(score, 1.0);
            } else if (normalizedCategory.contains(
                    "monument"
            )) {
                score = Math.max(score, 0.67);
            } else if (normalizedCategory.contains(
                    "fountain"
            )) {
                score = Math.max(score, 0.33);
            }
        }

        return score;
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

        return 1.0 / (1.0 + (distance / 1000.0));
    }
}