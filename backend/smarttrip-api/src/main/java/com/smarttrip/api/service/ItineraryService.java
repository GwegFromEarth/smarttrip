package com.smarttrip.api.service;

import com.smarttrip.api.dto.Itinerary;
import com.smarttrip.api.model.Trip;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ItineraryService {

    private final ChatClient chatClient;

    public ItineraryService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Itinerary generateItinerary(Trip trip) {
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
                        Itinerary.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );
    }
}