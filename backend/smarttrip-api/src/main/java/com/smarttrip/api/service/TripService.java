package com.smarttrip.api.service;

import com.smarttrip.api.dto.CreateTripRequest;
import com.smarttrip.api.dto.TripResponse;
import com.smarttrip.api.exception.InvalidTripException;
import com.smarttrip.api.exception.TripNotFoundException;
import com.smarttrip.api.model.Trip;
import com.smarttrip.api.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public TripResponse createTrip(CreateTripRequest request) {

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new InvalidTripException(
                    "Start date must be before or equal to end date"
            );
        }

        Trip trip = new Trip(
                request.getDestination(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTravelers(),
                request.getPreferences()
        );

        Trip savedTrip = tripRepository.save(trip);

        return toResponse(savedTrip);
    }

    public List<TripResponse> getTrips() {
        return tripRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TripResponse getTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        return toResponse(trip);
    }

    private TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getTravelers(),
                trip.getPreferences()
        );
    }
}