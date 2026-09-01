package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.springframework.stereotype.Component;

@Component
public class FoursquarePlaceMapper {

    public PlaceDto toPlaceDto(
            FoursquarePlace place,
            PlaceCategory category
    ) {
        String address = buildAddress(place);

        return new PlaceDto(
                place.fsq_id(),
                place.name(),
                null,
                place.latitude(),
                place.longitude(),
                category,
                address,
                place.distance() != null
                        ? place.distance().doubleValue()
                        : null,
                place.rating(),
                place.popularity()
        );
    }

    private String buildAddress(FoursquarePlace place) {

        if (place.location() == null) {
            return null;
        }

        FoursquareLocation location = place.location();

        StringBuilder address = new StringBuilder();

        append(address, location.address());
        append(address, location.postcode());
        append(address, location.locality());
        append(address, location.region());
        append(address, location.country());

        return address.isEmpty()
                ? null
                : address.toString();
    }

    private void append(
            StringBuilder builder,
            String value
    ) {
        if (value == null || value.isBlank()) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append(", ");
        }

        builder.append(value);
    }
}