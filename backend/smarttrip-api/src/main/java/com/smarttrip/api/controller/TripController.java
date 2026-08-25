package com.smarttrip.api.controller;

import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.smarttrip.api.dto.CreateTripRequest;
import com.smarttrip.api.dto.TripResponse;
import com.smarttrip.api.service.TripService;
import com.smarttrip.api.dto.ItineraryDto;
import com.smarttrip.api.service.ItineraryService;

import java.util.List;

@Tag(name = "Trips", description = "Gestion des voyages et des itinéraires")
@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final ItineraryService itineraryService;

    public TripController(
            TripService tripService,
            ItineraryService itineraryService) {

        this.tripService = tripService;
        this.itineraryService = itineraryService;
    }

    @Operation(
            summary = "Créer un voyage",
            description = "Crée un nouveau voyage SmartTrip à partir des informations fournies."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Voyage créé avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données du voyage invalides",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody CreateTripRequest request) {

        return ResponseEntity.ok(tripService.createTrip(request));
    }

    @Operation(
            summary = "Récupérer les voyages",
            description = "Retourne la liste de tous les voyages enregistrés."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des voyages récupérée avec succès"
    )
    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips() {
        return ResponseEntity.ok(tripService.getTrips());
    }

    @Operation(
            summary = "Récupérer un voyage",
            description = "Retourne un voyage à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Voyage trouvé"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Voyage introuvable",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable @Parameter(
            description = "Identifiant du voyage",
            example = "1"
    ) Long id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    @Operation(
            summary = "Générer un itinéraire",
            description = "Génère un itinéraire personnalisé pour un voyage à l'aide de l'assistant IA SmartTrip."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Itinéraire généré avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Voyage introuvable",
                    content = @Content
            )
    })
    @PostMapping("/{id}/itinerary")
    public ResponseEntity<ItineraryDto> generateItinerary(
            @PathVariable @Parameter(
                    description = "Identifiant du voyage",
                    example = "1"
            ) Long id) {

        return ResponseEntity.ok(
                itineraryService.generateItinerary(id)
        );
    }

    @Operation(
            summary = "Récupérer un itinéraire",
            description = "Retourne l'itinéraire actuellement enregistré pour un voyage."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Itinéraire récupéré avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinéraire ou voyage introuvable",
                    content = @Content
            )
    })
    @GetMapping("/{id}/itinerary")
    public ResponseEntity<ItineraryDto> getItinerary(@PathVariable @Parameter(
            description = "Identifiant du voyage",
            example = "1"
    ) Long id) {
        return ResponseEntity.ok(itineraryService.getItinerary(id));
    }
}