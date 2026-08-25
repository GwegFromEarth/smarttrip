package com.smarttrip.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Données nécessaires à la création d'un voyage")
public class CreateTripRequest {

    @NotBlank(message = "Destination is required")
    @Schema(
            description = "Destination du voyage",
            example = "Rome",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String destination;

    @NotNull(message = "Start date is required")
    @Schema(
            description = "Date de début du voyage",
            example = "2026-09-15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(
            description = "Date de fin du voyage",
            example = "2026-09-20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate endDate;

    @Min(value = 1, message = "Travelers must be at least 1")
    @Schema(
            description = "Nombre de voyageurs",
            example = "2",
            minimum = "1"
    )
    private int travelers;

    @Schema(
            description = "Préférences des voyageurs",
            example = "Histoire, gastronomie et visites culturelles"
    )
    private String preferences;

    public CreateTripRequest() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getTravelers() {
        return travelers;
    }

    public void setTravelers(int travelers) {
        this.travelers = travelers;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}