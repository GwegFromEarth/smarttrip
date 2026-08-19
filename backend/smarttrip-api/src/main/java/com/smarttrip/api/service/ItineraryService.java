package com.smarttrip.api.service;

import com.smarttrip.api.dto.ActivityDto;
import com.smarttrip.api.dto.ItineraryDayDto;
import com.smarttrip.api.dto.ItineraryDto;
import com.smarttrip.api.exception.TripNotFoundException;
import com.smarttrip.api.model.Activity;
import com.smarttrip.api.model.Itinerary;
import com.smarttrip.api.model.ItineraryDay;
import com.smarttrip.api.model.Trip;
import com.smarttrip.api.repository.ItineraryRepository;
import com.smarttrip.api.repository.TripRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItineraryService {

    private final ChatClient chatClient;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;

    public ItineraryService(
            ChatClient chatClient,
            TripRepository tripRepository,
            ItineraryRepository itineraryRepository) {

        this.chatClient = chatClient;
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
    }

    @Transactional
    public ItineraryDto generateItinerary(Long tripId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        ItineraryDto generated = generateItineraryFromAi(trip);

        Itinerary itinerary = new Itinerary(
                trip,
                generated.destination()
        );

        for (ItineraryDayDto dayDto : generated.days()) {

            ItineraryDay day = new ItineraryDay(
                    dayDto.dayNumber(),
                    dayDto.date()
            );

            for (ActivityDto activityDto : dayDto.activities()) {

                Activity activity = new Activity(
                        activityDto.time(),
                        activityDto.title(),
                        activityDto.description(),
                        activityDto.location()
                );

                day.addActivity(activity);
            }

            itinerary.addDay(day);
        }

        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        return toDto(savedItinerary);
    }

    private ItineraryDto generateItineraryFromAi(Trip trip) {

        String prompt = """
            Génère un itinéraire de voyage pour les informations suivantes :

            Destination : %s
            Date de début : %s
            Date de fin : %s
            Nombre de voyageurs : %d
            Préférences : %s

            Pour ce test, génère uniquement les 2 premiers jours du voyage.
            Propose exactement 2 activités par jour.
            Les descriptions doivent être courtes, avec une ou deux phrases maximum.

            Respecte strictement la structure JSON demandée.
            Ne génère aucun texte avant ou après le JSON.
            """.formatted(
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getTravelers(),
                trip.getPreferences()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(
                        ItineraryDto.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );
    }

    private ItineraryDto toDto(Itinerary itinerary) {

        return new ItineraryDto(
                itinerary.getTrip().getId(),
                itinerary.getDestination(),
                itinerary.getDays()
                        .stream()
                        .map(day -> new ItineraryDayDto(
                                day.getDayNumber(),
                                day.getDate(),
                                day.getActivities()
                                        .stream()
                                        .map(activity -> new ActivityDto(
                                                activity.getTime(),
                                                activity.getTitle(),
                                                activity.getDescription(),
                                                activity.getLocation()
                                        ))
                                        .toList()
                        ))
                        .toList()
        );
    }
}