package com.smarttrip.api.service;

import com.smarttrip.api.repository.ItineraryRepository;
import com.smarttrip.api.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.mockito.Mockito.mock;

class ItineraryServiceTest {

    private final ChatClient chatClient = mock(ChatClient.class);
    private final TripRepository tripRepository = mock(TripRepository.class);
    private final ItineraryRepository itineraryRepository =
            mock(ItineraryRepository.class);
    private final PlaceService placeService = mock(PlaceService.class);

    @Test
    void shouldUsePlaceServiceForItineraryGeneration() {

        ItineraryService service = new ItineraryService(
                chatClient,
                tripRepository,
                itineraryRepository,
                placeService
        );
    }
}