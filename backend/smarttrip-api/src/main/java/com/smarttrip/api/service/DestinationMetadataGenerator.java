package com.smarttrip.api.service;

import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DestinationMetadataGenerator
        implements JsonMetadataGenerator {

    private static final String SOURCE =
            "smarttrip-destinations";

    @Override
    public Map<String, Object> generate(
            Map<String, Object> jsonMap
    ) {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put(
                "source",
                SOURCE
        );

        metadata.put(
                "destination",
                jsonMap.get("destination")
        );

        metadata.put(
                "topic",
                jsonMap.get("topic")
        );

        return metadata;
    }
}