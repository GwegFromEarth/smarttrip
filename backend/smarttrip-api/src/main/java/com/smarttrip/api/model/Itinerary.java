package com.smarttrip.api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    private Trip trip;

    @Column(nullable = false)
    private String destination;

    @OneToMany(
            mappedBy = "itinerary",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItineraryDay> days = new ArrayList<>();

    protected Itinerary() {
    }

    public Itinerary(Trip trip, String destination) {
        this.trip = trip;
        this.destination = destination;
    }

    public Long getId() {
        return id;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getDestination() {
        return destination;
    }

    public List<ItineraryDay> getDays() {
        return days;
    }

    public void addDay(ItineraryDay day) {
        days.add(day);
        day.setItinerary(this);
    }
}