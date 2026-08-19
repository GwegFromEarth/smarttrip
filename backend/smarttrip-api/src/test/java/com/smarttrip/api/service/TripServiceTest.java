package com.smarttrip.api.service;

import com.smarttrip.api.dto.CreateTripRequest;
import com.smarttrip.api.exception.InvalidTripException;
import com.smarttrip.api.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    void createTrip_shouldRejectWhenStartDateIsAfterEndDate() {
        CreateTripRequest request = new CreateTripRequest();
        request.setDestination("Rome");
        request.setStartDate(LocalDate.of(2026, 9, 17));
        request.setEndDate(LocalDate.of(2026, 9, 12));
        request.setTravelers(2);
        request.setPreferences("culture");

        assertThrows(
                InvalidTripException.class,
                () -> tripService.createTrip(request)
        );
    }
}