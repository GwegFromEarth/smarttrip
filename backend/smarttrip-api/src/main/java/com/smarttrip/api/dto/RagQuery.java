package com.smarttrip.api.dto;

public record RagQuery(
        boolean ragRelevant,
        String destination,
        String topic
) {
}