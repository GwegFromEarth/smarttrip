package com.smarttrip.api.ai;

import com.smarttrip.api.dto.PlaceDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaceToolContext {

    private final List<PlaceDto> places = new ArrayList<>();

    public void addPlaces(List<PlaceDto> newPlaces) {
        if (newPlaces != null) {
            places.addAll(newPlaces);
        }
    }

    public List<PlaceDto> getPlaces() {
        return Collections.unmodifiableList(places);
    }
}