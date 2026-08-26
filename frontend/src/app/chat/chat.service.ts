import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface StreamEvent {
  type: 'conversation' | 'content';
  data: string;
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

      console.log('Requête envoyée au backend :', request);

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

            // Un événement SSE est séparé du suivant
            // par une ligne vide.
            const events = buffer.split('\n\n');

            buffer = events.pop() ?? '';

            for (const event of events) {

              if (!event.trim()) {
                continue;
              }

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
                  const rawData = line.substring(5);

                  data += rawData;
                }
              }

              if (data) {

                subscriber.next({
                  type: eventType === 'conversation'
                    ? 'conversation'
                    : 'content',
                  data
                });
              }
            }
          }

          // Traiter éventuellement le dernier événement
          // si le serveur ne termine pas par \n\n.
          if (buffer.trim()) {

            console.log(
              'DERNIER ÉVÉNEMENT SSE :',
              JSON.stringify(buffer)
            );

            let eventType = 'content';
            let data = '';

            for (const line of buffer.split('\n')) {

              if (line.startsWith('event:')) {
                eventType = line
                  .substring(6)
                  .trim();
              }

              if (line.startsWith('data:')) {
                const rawData = line.substring(5);

                data += rawData;
              }
            }

            if (data) {

              subscriber.next({
                type: eventType === 'conversation'
                  ? 'conversation'
                  : 'content',
                data
              });
            }
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
}