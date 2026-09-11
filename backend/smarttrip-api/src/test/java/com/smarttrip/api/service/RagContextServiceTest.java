package com.smarttrip.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RagContextServiceTest {

    private RagSearchService ragSearchService;
    private RagContextService ragContextService;

    @BeforeEach
    void setUp() {

        ragSearchService =
                mock(RagSearchService.class);

        ragContextService =
                new RagContextService(ragSearchService);
    }

    @Test
    void shouldBuildContextFromDocuments() {

        // =========================================================
        // GIVEN
        // =========================================================

        Document document1 =
                new Document(
                        "Rome possède un patrimoine historique exceptionnel."
                );

        Document document2 =
                new Document(
                        "Le Colisée et le Forum romain sont des sites majeurs."
                );

        when(
                ragSearchService.search(
                        "lieux historiques"
                )
        ).thenReturn(
                List.of(
                        document1,
                        document2
                )
        );

        // =========================================================
        // WHEN
        // =========================================================

        String context =
                ragContextService.buildContext(
                        "lieux historiques"
                );

        // =========================================================
        // THEN
        // =========================================================

        assertThat(context)
                .isNotBlank();

        assertThat(context)
                .contains(
                        "Rome possède un patrimoine historique exceptionnel."
                );

        assertThat(context)
                .contains(
                        "Le Colisée et le Forum romain sont des sites majeurs."
                );

        assertThat(context)
                .contains("\n\n");

        verify(ragSearchService)
                .search("lieux historiques");

        verifyNoMoreInteractions(ragSearchService);
    }

    @Test
    void shouldBuildContextWithDestination() {

        // =========================================================
        // GIVEN
        // =========================================================

        Document document =
                new Document(
                        "Rome est une destination majeure pour découvrir "
                                + "l'histoire antique."
                );

        when(
                ragSearchService.search(
                        "histoire",
                        "Rome"
                )
        ).thenReturn(
                List.of(document)
        );

        // =========================================================
        // WHEN
        // =========================================================

        String context =
                ragContextService.buildContext(
                        "histoire",
                        "Rome"
                );

        // =========================================================
        // THEN
        // =========================================================

        assertThat(context)
                .isEqualTo(
                        "Rome est une destination majeure pour découvrir "
                                + "l'histoire antique."
                );

        verify(ragSearchService)
                .search(
                        "histoire",
                        "Rome"
                );

        verifyNoMoreInteractions(ragSearchService);
    }

    @Test
    void shouldReturnEmptyContextWhenNoDocumentsAreFound() {

        // =========================================================
        // GIVEN
        // =========================================================

        when(
                ragSearchService.search(
                        "histoire"
                )
        ).thenReturn(
                List.of()
        );

        // =========================================================
        // WHEN
        // =========================================================

        String context =
                ragContextService.buildContext(
                        "histoire"
                );

        // =========================================================
        // THEN
        // =========================================================

        assertThat(context)
                .isEmpty();

        verify(ragSearchService)
                .search("histoire");

        verifyNoMoreInteractions(ragSearchService);
    }

    @Test
    void shouldIgnoreDocumentsWithEmptyText() {

        // =========================================================
        // GIVEN
        // =========================================================

        Document emptyDocument =
                new Document("");

        Document validDocument =
                new Document(
                        "Le Panthéon est un monument historique majeur."
                );

        when(
                ragSearchService.search(
                        "monuments"
                )
        ).thenReturn(
                List.of(
                        emptyDocument,
                        validDocument
                )
        );

        // =========================================================
        // WHEN
        // =========================================================

        String context =
                ragContextService.buildContext(
                        "monuments"
                );

        // =========================================================
        // THEN
        // =========================================================

        assertThat(context)
                .isEqualTo(
                        "Le Panthéon est un monument historique majeur."
                );

        verify(ragSearchService)
                .search("monuments");

        verifyNoMoreInteractions(ragSearchService);
    }

    @Test
    void shouldReturnEmptyContextWhenSearchReturnsNull() {

        // =========================================================
        // GIVEN
        // =========================================================

        when(
                ragSearchService.search(
                        "inconnu"
                )
        ).thenReturn(null);

        // =========================================================
        // WHEN
        // =========================================================

        String context =
                ragContextService.buildContext(
                        "inconnu"
                );

        // =========================================================
        // THEN
        // =========================================================

        assertThat(context)
                .isEmpty();

        verify(ragSearchService)
                .search("inconnu");

        verifyNoMoreInteractions(ragSearchService);
    }
}