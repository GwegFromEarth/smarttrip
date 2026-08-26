package com.smarttrip.api.controller;

import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@Tag(
        name = "Places",
        description = "Recherche de lieux touristiques autour d'une position"
)
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    @Operation(
            summary = "Rechercher des lieux",
            description = "Recherche des lieux touristiques autour d'une position géographique"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lieux trouvés",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PlaceDto.class)
                            )
                    )
            )
    })
    public List<PlaceDto> search(
            @Parameter(
                    description = "Latitude du point de recherche",
                    example = "48.8606"
            )
            @RequestParam double latitude,

            @Parameter(
                    description = "Longitude du point de recherche",
                    example = "2.3376"
            )
            @RequestParam double longitude,

            @Parameter(
                    description = "Rayon de recherche en mètres",
                    example = "1000"
            )
            @RequestParam(defaultValue = "1000") int radius,

            @Parameter(
                    description = "Catégorie Geoapify",
                    example = "entertainment.museum"
            )
            @RequestParam String category,

            @Parameter(
                    description = "Nombre maximum de résultats",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int limit
    ) {
        return placeService.search(
                latitude,
                longitude,
                radius,
                category,
                limit
        );
    }
}