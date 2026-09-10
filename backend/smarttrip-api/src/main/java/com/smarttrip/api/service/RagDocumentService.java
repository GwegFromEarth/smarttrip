package com.smarttrip.api.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagDocumentService {

    private final VectorStore vectorStore;
    private final Resource destinationsResource;
    private final DestinationMetadataGenerator metadataGenerator;

    public RagDocumentService(
            VectorStore vectorStore,
            @Value("classpath:rag/destinations.json")
            Resource destinationsResource,
            DestinationMetadataGenerator metadataGenerator
    ) {
        this.vectorStore = vectorStore;
        this.destinationsResource = destinationsResource;
        this.metadataGenerator = metadataGenerator;
    }

    public int indexDestinations() {

        deleteDestinations();

        JsonReader reader = new JsonReader(
                destinationsResource,
                metadataGenerator,
                "content"
        );

        List<Document> documents = reader.get();

        vectorStore.add(documents);

        return documents.size();
    }

    public void deleteDestinations() {

        Filter.Expression filter =
                new Filter.Expression(
                        Filter.ExpressionType.EQ,
                        new Filter.Key("source"),
                        new Filter.Value("smarttrip-destinations")
                );

        vectorStore.delete(filter);
    }
}