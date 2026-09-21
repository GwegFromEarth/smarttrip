import {
  Component,
  ElementRef,
  afterRenderEffect,
  inject,
  signal,
  viewChild
} from '@angular/core';

import { Subscription } from 'rxjs';
import { MarkdownComponent } from 'ngx-markdown';

import {
  ChatService,
  PlaceDto
} from './chat.service';
import { ConversationService } from '../conversation/conversation.service';
import { ConversationList } from '../conversation/conversation-list/conversation-list';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  streaming?: boolean;
}

@Component({
  selector: 'app-chat',
  imports: [
    MarkdownComponent,
    ConversationList
  ],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class Chat {

  private readonly chatService = inject(ChatService);
  private readonly conversationService = inject(ConversationService);
  private streamSubscription?: Subscription;

  message = signal('');
  response = signal('');

  streamResponse = signal('');

  conversationId = signal<number | null>(null);

  messages = signal<ChatMessage[]>([]);
  places = signal<PlaceDto[]>([]);

  isStreaming = signal(false);
  errorMessage = signal<string | null>(null);

  conversationElement =
    viewChild<ElementRef<HTMLDivElement>>('conversation');

  constructor() {

    afterRenderEffect({

      earlyRead: () => {

        this.messages();

        const element =
          this.conversationElement()?.nativeElement;

        if (!element) {
          return null;
        }

        return {
          element,
          scrollHeight: element.scrollHeight
        };
      },

      write: (data) => {

        const value = data();

        if (!value) {
          return;
        }

        value.element.scrollTop = value.scrollHeight;
      }
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

  private formatMarkdown(content: string): string {

    return content
      // Liste collée au texte précédent :
      // "Rome :1. **Villa Borghese**"
      // devient :
      // "Rome :\n\n1. **Villa Borghese**"
      .replace(
        /([.!?:])\s*(?=\d+\.\s+\*\*)/g,
        '$1\n\n'
      )

      // Éléments de liste collés :
      // "... Borghese.2. **Villa Doria Pamphili**"
      // devient :
      // "... Borghese.\n\n2. **Villa Doria Pamphili**"
      .replace(
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
    this.errorMessage.set(null);

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

    this.streamSubscription =
      this.chatService
      .streamChat(this.conversationId(), message)
      .subscribe({

        next: event => {

          // =========================================================
          // CONVERSATION
          // =========================================================

          if (event.type === 'conversation') {

            const conversationId =
              Number(event.data);

            this.conversationId.set(conversationId);

            return;
          }

          // =========================================================
          // PLACES
          // =========================================================

          if (event.type === 'places') {

            this.places.set(event.places ?? []);

            console.log(
              'Places reçues dans le composant :',
              this.places()
            );

            return;
          }

          // =========================================================
          // CONTENT
          // =========================================================

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

          this.errorMessage.set(
            'Une erreur est survenue pendant la réponse. Veuillez réessayer.'
          );

          this.isStreaming.set(false);

          this.streamSubscription = undefined;

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

stopStreaming(): void {
    if (!this.isStreaming()) {
      return;
    }

    this.streamSubscription?.unsubscribe();
    this.streamSubscription = undefined;

    this.isStreaming.set(false);

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

  onConversationSelected(
    conversationId: number
  ): void {

    this.conversationId.set(conversationId);

    this.conversationService
      .getMessages(conversationId)
      .subscribe({
        next: messages => {
          this.messages.set(
            messages.map(message => ({
              role: message.role,
              content: message.content
            }))
          );
        },

        error: error => {
          console.error(
            'Erreur lors du chargement de la conversation :',
            error
          );

          this.errorMessage.set(
            'Impossible de charger cette conversation.'
          );
        }
      });
  }

  onNewConversation(): void {

    this.streamSubscription?.unsubscribe();
    this.streamSubscription = undefined;

    this.conversationId.set(null);
    this.messages.set([]);
    this.places.set([]);
    this.streamResponse.set('');
    this.response.set('');
    this.message.set('');
    this.errorMessage.set(null);
    this.isStreaming.set(false);
  }
}