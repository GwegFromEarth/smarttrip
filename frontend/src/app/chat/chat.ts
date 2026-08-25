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

    // Ajouter une réponse assistant vide.
    // Pendant le streaming, elle sera affichée en texte brut.
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

          if (event.type === 'conversation') {

            const conversationId =
              Number(event.data);

            this.conversationId.set(conversationId);

            return;
          }

          console.log(
            'CHUNK SSE :',
            JSON.stringify(event.data)
          );

          this.streamResponse.update(
            current => current + event.data
          );

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
           * On passe le dernier message assistant
           * de streaming=true à streaming=false.
           *
           * Le template va alors remplacer le texte brut
           * par ngx-markdown.
           */
          this.messages.update(current => {

            const updated = [...current];

            const lastMessage =
              updated[updated.length - 1];

            if (lastMessage?.role === 'assistant') {

              updated[updated.length - 1] = {
                ...lastMessage,
                content: this.streamResponse(),
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