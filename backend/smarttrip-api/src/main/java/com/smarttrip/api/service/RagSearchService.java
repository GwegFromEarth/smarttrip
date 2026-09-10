package com.smarttrip.api.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagSearchService {

    private static final String SOURCE = "smarttrip-destinations";

    private static final int DEFAULT_TOP_K = 3;

    private final VectorStore vectorStore;

    public RagSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> search(String query) {

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(DEFAULT_TOP_K)
                        .filterExpression(
                                "source == '" + SOURCE + "'"
                        )
                        .build()
        );
    }

    public List<Document> search(
            String query,
            String destination
    ) {

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(DEFAULT_TOP_K)
                        .filterExpression(
                                "source == '" + SOURCE + "'" +
                                        " && destination == '" +
                                        destination + "'"
                        )
                        .build()
        );
    }
}