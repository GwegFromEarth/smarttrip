package com.smarttrip.api.service;

import com.smarttrip.api.dto.ActivityDto;
import com.smarttrip.api.dto.ItineraryDayDto;
import com.smarttrip.api.dto.ItineraryDto;
import com.smarttrip.api.exception.ItineraryNotFoundException;
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
    private final PlaceService placeService;

    public ItineraryService(
            ChatClient chatClient,
            TripRepository tripRepository,
            ItineraryRepository itineraryRepository,
            PlaceService placeService) {

        this.chatClient = chatClient;
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
        this.placeService = placeService;
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

    @Transactional(readOnly = true)
    public ItineraryDto getItinerary(Long tripId) {

        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ItineraryNotFoundException(tripId));

        return toDto(itinerary);
    }

    private ItineraryDto generateItineraryFromAi(Trip trip) {

        String prompt = """
            Génère un itinéraire de voyage réaliste pour les informations suivantes :
        
            Destination : %s
            Date de début : %s
            Date de fin : %s
            Nombre de voyageurs : %d
            Préférences : %s
        
            Règles obligatoires :
        
            - Génère uniquement les 2 premiers jours du voyage pour ce test.
            - Génère exactement 2 activités par jour.
            - Utilise uniquement des lieux, monuments, musées ou quartiers qui existent réellement.
            - N'invente jamais de lieu, de monument, de musée, de restaurant ou d'attraction.
            - Utilise les noms officiels ou les noms touristiques couramment utilisés.
            - Les activités doivent être réellement accessibles dans la destination indiquée.
            - Regroupe autant que possible les activités proches géographiquement le même jour.
            - Propose des horaires réalistes et cohérents avec les activités.
            - Évite de programmer deux activités qui se chevauchent.
            - Les descriptions doivent être courtes : une ou deux phrases maximum.
            - Ne mentionne pas d'informations dont tu n'es pas suffisamment certain.
            - Respecte strictement la destination, les dates, le nombre de voyageurs et les préférences fournies.
        
            Réponds uniquement avec les données correspondant à la structure JSON demandée.
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