import {
  Component,
  ElementRef,
  effect,
  inject,
  signal,
  viewChild
} from '@angular/core';

import { MarkdownComponent } from 'ngx-markdown';

import { ChatService } from './chat.service';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  streaming?: boolean;
}

@Component({
  selector: 'app-chat',
  imports: [MarkdownComponent],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class Chat {

  private readonly chatService = inject(ChatService);

  message = signal('');
  response = signal('');

  streamResponse = signal('');

  conversationId = signal<number | null>(null);

  messages = signal<ChatMessage[]>([]);

  isStreaming = signal(false);

  conversationElement =
    viewChild<ElementRef<HTMLDivElement>>('conversation');

  constructor() {

    effect(() => {

      this.streamResponse();
      this.messages();

      requestAnimationFrame(() => {
        this.scrollConversationToBottom();
      });

    });
  }

  private scrollConversationToBottom(): void {

    const element =
      this.conversationElement()?.nativeElement;

    if (!element) {
      return;
    }

    element.scrollTop = element.scrollHeight;
  }

  /**
   * Normalise certaines listes Markdown générées
   * sans retour à la ligne entre les éléments.
   *
   * Exemple :
   *
   * 1. **Colisée** ... Rome.2. **Panthéon** ... romaine.3. **Forum**
   *
   * devient :
   *
   * 1. **Colisée** ... Rome.
   *
   * 2. **Panthéon** ... romaine.
   *
   * 3. **Forum**
   */
  private formatMarkdown(content: string): string {

    return content.replace(
      /([.!?])\s*(?=\d+\.\s+\*\*)/g,
      '$1\n\n'
    );
  }

  sendStreamMessage(): void {

    const message = this.message().trim();

    if (!message || this.isStreaming()) {
      return;
    }

    this.isStreaming.set(true);

    this.streamResponse.set('');

    // Ajouter le message utilisateur
    this.messages.update(current => [
      ...current,
      {
        role: 'user',
        content: message
      }
    ]);

    // Ajouter immédiatement une réponse assistant vide.
    // Pendant le streaming, elle est affichée en texte brut.
    this.messages.update(current => [
      ...current,
      {
        role: 'assistant',
        content: '',
        streaming: true
      }
    ]);

    this.chatService
      .streamChat(this.conversationId(), message)
      .subscribe({

        next: event => {

          // Premier événement envoyé par le backend :
          // récupération de l'identifiant de conversation.
          if (event.type === 'conversation') {

            const conversationId =
              Number(event.data);

            this.conversationId.set(conversationId);

            return;
          }

          // Ajouter le chunk reçu à la réponse complète.
          this.streamResponse.update(
            current => current + event.data
          );

          // Mettre à jour le dernier message assistant.
          this.messages.update(current => {

            const updated = [...current];

            const lastMessage =
              updated[updated.length - 1];

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

          /*
           * Le streaming est terminé.
           *
           * On normalise le Markdown uniquement maintenant.
           * Cela évite de modifier le contenu à chaque chunk.
           */
          const formattedResponse =
            this.formatMarkdown(
              this.streamResponse()
            );

          this.messages.update(current => {

            const updated = [...current];

            const lastMessage =
              updated[updated.length - 1];

            if (lastMessage?.role === 'assistant') {

              updated[updated.length - 1] = {
                ...lastMessage,
                content: formattedResponse,
                streaming: false
              };
            }

            return updated;
          });

          this.isStreaming.set(false);
          this.message.set('');
        }
      });
  }

  onEnter(event: Event): void {

    const keyboardEvent =
      event as KeyboardEvent;

    if (keyboardEvent.shiftKey) {
      return;
    }

    keyboardEvent.preventDefault();

    this.sendStreamMessage();
  }
}