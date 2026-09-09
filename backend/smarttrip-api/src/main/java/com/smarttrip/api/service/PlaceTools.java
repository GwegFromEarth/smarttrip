package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.foursquare.FoursquarePlaceService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaceTools {

    private final FoursquarePlaceService foursquarePlaceService;

    public PlaceTools(FoursquarePlaceService foursquarePlaceService) {
        this.foursquarePlaceService = foursquarePlaceService;
    }

    @Tool(
            description = "Recherche les principaux lieux touristiques à visiter dans une destination. " +
                    "Utilise cet outil lorsqu'un utilisateur demande quoi visiter, " +
                    "quels monuments découvrir ou quels lieux touristiques voir dans une ville."
    )
    public List<PlaceDto> searchTouristAttractions(String destination) {

        return foursquarePlaceService.searchByDestination(
                destination,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        );
    }

    @Tool(
            description = "Recherche les principaux musées à visiter dans une destination. " +
                    "Utilise cet outil lorsqu'un utilisateur demande quels musées visiter " +
                    "ou souhaite découvrir des musées dans une ville."
    )
    public List<PlaceDto> searchMuseums(String destination) {

        return foursquarePlaceService.searchByDestination(
                destination,
                PlaceCategory.MUSEUM,
                10
        );
    }

    @Tool(
            description = "Recherche les principaux restaurants dans une destination. " +
                    "Utilise cet outil lorsqu'un utilisateur demande des restaurants, " +
                    "où manger ou souhaite trouver des restaurants dans une ville."
    )
    public List<PlaceDto> searchRestaurants(String destination) {

        return foursquarePlaceService.searchByDestination(
                destination,
                PlaceCategory.RESTAURANT,
                10
        );
    }

    @Tool(
            description = "Recherche les principaux cafés dans une destination. " +
                    "Utilise cet outil lorsqu'un utilisateur demande des cafés, " +
                    "un endroit pour prendre un café ou souhaite trouver des cafés dans une ville."
    )
    public List<PlaceDto> searchCafes(String destination) {

        return foursquarePlaceService.searchByDestination(
                destination,
                PlaceCategory.CAFE,
                10
        );
    }

    @Tool(
            description = "Recherche les principaux parcs dans une destination. " +
                    "Utilise cet outil lorsqu'un utilisateur demande des parcs, " +
                    "des espaces verts ou souhaite trouver des parcs dans une ville."
    )
    public List<PlaceDto> searchParks(String destination) {

        System.out.println(">>> TOOL searchParks APPELE pour : " + destination);

        return foursquarePlaceService.searchByDestination(
                destination,
                PlaceCategory.PARK,
                10
        );
    }
}