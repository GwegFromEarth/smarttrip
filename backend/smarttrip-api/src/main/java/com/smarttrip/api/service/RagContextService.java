package com.smarttrip.api.service;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagContextService {

    private final RagSearchService ragSearchService;

    public RagContextService(RagSearchService ragSearchService) {
        this.ragSearchService = ragSearchService;
    }

    public String buildContext(String query) {

        List<Document> documents =
                ragSearchService.search(query);

        return buildContext(documents);
    }

    public String buildContext(
            String query,
            String destination
    ) {

        List<Document> documents =
                ragSearchService.search(
                        query,
                        destination
                );

        return buildContext(documents);
    }

    private String buildContext(List<Document> documents) {

        if (documents == null || documents.isEmpty()) {
            return "";
        }

        return documents.stream()
                .map(Document::getText)
                .filter(text -> text != null && !text.isBlank())
                .collect(Collectors.joining("\n\n"));
    }
}