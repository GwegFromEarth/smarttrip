import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PlaceDto {
  placeId: string;
  name: string;
  description: string | null;
  latitude: number;
  longitude: number;
  category: string;
  address: string | null;
  distance: number | null;
  rating: number | null;
  popularity: number | null;
  tel: string | null;
  website: string | null;
  categories: string[];
}

export interface StreamEvent {
  type: 'conversation' | 'content' | 'places';
  data: string;
  places?: PlaceDto[];
}

export interface ChatRequest {
  conversationId: number | null;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private readonly apiUrl = 'http://localhost:8080/api/chat';

  streamChat(
    conversationId: number | null,
    message: string
  ): Observable<StreamEvent> {

    return new Observable<StreamEvent>(subscriber => {

      const controller = new AbortController();

      const request: ChatRequest = {
        conversationId,
        message
      };

      console.log(
        'Requête envoyée au backend :',
        request
      );

      fetch(`${this.apiUrl}/stream`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(request),
        signal: controller.signal
      })
        .then(async response => {

          console.log(
            'Réponse HTTP :',
            response.status,
            response.headers.get('content-type')
          );

          if (!response.ok) {
            throw new Error(
              `Erreur HTTP ${response.status} : ${response.statusText}`
            );
          }

          if (!response.body) {
            throw new Error(
              'Le navigateur ne supporte pas le streaming.'
            );
          }

          const reader = response.body.getReader();
          const decoder = new TextDecoder('utf-8');

          let buffer = '';

          while (true) {

            const { value, done } = await reader.read();

            if (done) {
              break;
            }

            buffer += decoder.decode(value, {
              stream: true
            });

            const events = buffer.split('\n\n');

            buffer = events.pop() ?? '';

            for (const event of events) {

              if (!event.trim()) {
                continue;
              }

              this.processSseEvent(
                event,
                subscriber
              );
            }
          }

          // Dernier événement éventuel
          if (buffer.trim()) {

            console.log(
              'DERNIER ÉVÉNEMENT SSE :',
              JSON.stringify(buffer)
            );

            this.processSseEvent(
              buffer,
              subscriber
            );
          }

          subscriber.complete();
        })
        .catch(error => {

          if (error.name !== 'AbortError') {

            console.error(
              'Erreur SSE :',
              error
            );

            subscriber.error(error);
          }
        });

      return () => {
        controller.abort();
      };
    });
  }

  private processSseEvent(
    event: string,
    subscriber: {
      next: (value: StreamEvent) => void;
    }
  ): void {

    let eventType = 'content';
    let data = '';

    const lines = event.split('\n');

    for (const line of lines) {

      if (line.startsWith('event:')) {

        eventType = line
          .substring(6)
          .trim();
      }

      if (line.startsWith('data:')) {

        const rawData =
          line.substring(5);

        data += rawData;
      }
    }

    if (!data) {
      return;
    }

    if (eventType === 'conversation') {

      subscriber.next({
        type: 'conversation',
        data
      });

      return;
    }

    if (eventType === 'places') {

      try {

        const places: PlaceDto[] =
          JSON.parse(data);

        console.log(
          'Lieux reçus depuis le backend :',
          places
        );

        subscriber.next({
          type: 'places',
          data,
          places
        });

      } catch (error) {

        console.error(
          'Impossible de parser les lieux reçus :',
          error,
          data
        );
      }

      return;
    }

    subscriber.next({
      type: 'content',
      data
    });
  }
}