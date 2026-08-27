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
                        Comparator.comparing(
                                PlaceDto::distance,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()
                                )
                        )
                )
                .limit(limit)
                .toList();
    }
}