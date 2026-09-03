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

        FoursquareCoordinates coordinates =
                extractCoordinates(place);

        return new PlaceDto(
                place.fsq_id(),
                place.name(),
                null,
                coordinates.latitude(),
                coordinates.longitude(),
                category,
                address,
                place.distance() != null
                        ? place.distance().doubleValue()
                        : null,
                place.rating(),
                place.popularity()
        );
    }

    private FoursquareCoordinates extractCoordinates(
            FoursquarePlace place
    ) {
        // Nouveau format Foursquare :
        // les coordonnées sont directement sur le place.
        if (place.latitude() != null || place.longitude() != null) {

            if (place.latitude() == null
                    || place.longitude() == null) {

                throw new IllegalArgumentException(
                        "Foursquare place has incomplete coordinates"
                );
            }

            return new FoursquareCoordinates(
                    place.latitude(),
                    place.longitude()
            );
        }

        // Fallback : ancien/autre format avec geocodes.main.
        if (place.geocodes() != null
                && place.geocodes().main() != null) {

            FoursquareCoordinates coordinates =
                    place.geocodes().main();

            if (coordinates.latitude() == null
                    || coordinates.longitude() == null) {

                throw new IllegalArgumentException(
                        "Foursquare place has incomplete coordinates"
                );
            }

            return coordinates;
        }

        throw new IllegalArgumentException(
                "Foursquare place has no coordinates"
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