package com.smarttrip.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ItineraryDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @Column(nullable = false)
    private int dayNumber;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(
            mappedBy = "day",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Activity> activities = new ArrayList<>();

    protected ItineraryDay() {
    }

    public ItineraryDay(int dayNumber, LocalDate date) {
        this.dayNumber = dayNumber;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        activity.setDay(this);
    }
}