package com.smarttrip.api.dto;

public enum PlaceCategory {

    TOURIST_ATTRACTION("tourism"),
    MUSEUM("entertainment.museum"),
    RESTAURANT("catering.restaurant"),
    CAFE("catering.cafe"),
    PARK("leisure.park");

    private final String geoapifyCategory;

    PlaceCategory(String geoapifyCategory) {
        this.geoapifyCategory = geoapifyCategory;
    }

    public String geoapifyCategory() {
        return geoapifyCategory;
    }
}