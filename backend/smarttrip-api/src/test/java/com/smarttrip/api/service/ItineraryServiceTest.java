package com.smarttrip.api.service;

import com.smarttrip.api.repository.ItineraryRepository;
import com.smarttrip.api.repository.TripRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class ItineraryServiceTest {

    private final AiChatService aiChatService =
            mock(AiChatService.class);

    private final TripRepository tripRepository =
            mock(TripRepository.class);

    private final ItineraryRepository itineraryRepository =
            mock(ItineraryRepository.class);

    private final PlaceService placeService =
            mock(PlaceService.class);

    @Test
    void shouldUsePlaceServiceForItineraryGeneration() {

        ItineraryService service = new ItineraryService(
                aiChatService,
                tripRepository,
                itineraryRepository,
                placeService
        );
    }
}