import { Component, inject, signal } from '@angular/core';
import { ChatService } from './chat.service';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

@Component({
  selector: 'app-chat',
  imports: [],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class Chat {

  private readonly chatService = inject(ChatService);

  message = signal('');
  response = signal('');

  streamMessage = signal('');
  streamResponse = signal('');

  conversationId = signal<number | null>(null);

  messages = signal<ChatMessage[]>([]);

  isStreaming = signal(false);

  sendMessage(): void {
    const message = this.message().trim();

    if (!message) {
      return;
    }

    this.response.set('Réflexion en cours...');

    // Ancienne méthode conservée pour le moment.
    // Elle pourra être supprimée lorsque nous aurons
    // complètement migré vers le streaming.
  }

  sendStreamMessage(): void {
    const message = this.message().trim();

    if (!message || this.isStreaming()) {
      return;
    }

    this.isStreaming.set(true);

    this.response.set('');

    // Ajouter le message utilisateur à l'affichage
    this.messages.update(current => [
      ...current,
      {
        role: 'user',
        content: message
      }
    ]);

    // Préparer la réponse de l'assistant
    this.streamResponse.set('');

    // Ajouter immédiatement une réponse assistant vide.
    // Elle sera remplie progressivement pendant le streaming.
    this.messages.update(current => [
      ...current,
      {
        role: 'assistant',
        content: ''
      }
    ]);

    this.chatService
      .streamChat(this.conversationId(), message)
      .subscribe({

        next: event => {

          if (event.type === 'conversation') {

            const conversationId = Number(event.data);

            this.conversationId.set(conversationId);

            return;
          }

          this.streamResponse.update(
            current => current + event.data
          );

          this.messages.update(current => {
            const updated = [...current];

            const lastMessage = updated[updated.length - 1];

            if (lastMessage?.role === 'assistant') {
              updated[updated.length - 1] = {
                ...lastMessage,
                content: this.streamResponse()
              };
            }

            return updated;
          });
        },

        error: error => {
          console.error(
            'Erreur lors du streaming :',
            error
          );

          this.isStreaming.set(false);
        },

        complete: () => {
          this.isStreaming.set(false);
          this.message.set('');
        }
      });
  }

  onEnter(event: Event): void {
    const keyboardEvent = event as KeyboardEvent;

    if (keyboardEvent.shiftKey) {
      return;
    }

    keyboardEvent.preventDefault();

    this.sendStreamMessage();
  }
}