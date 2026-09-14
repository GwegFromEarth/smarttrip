import {
  Component,
  inject,
  OnInit,
  output,
  signal
} from '@angular/core';

import { DatePipe } from '@angular/common';

import {
  Conversation,
  ConversationMessage
} from '../conversation.models';

import { ConversationService } from '../conversation.service';

@Component({
  selector: 'app-conversation-list',
  imports: [DatePipe],
  templateUrl: './conversation-list.html',
  styleUrl: './conversation-list.css'
})
export class ConversationList implements OnInit {

  private readonly conversationService =
    inject(ConversationService);

  conversations =
    signal<Conversation[]>([]);

  selectedConversationId =
    signal<number | null>(null);

  conversationSelected = output<number>();

  loading =
    signal(false);

  errorMessage =
    signal<string | null>(null);

  ngOnInit(): void {

    this.loadConversations();
  }

  loadConversations(): void {

    this.loading.set(true);
    this.errorMessage.set(null);

    this.conversationService
      .getConversations()
      .subscribe({

        next: conversations => {

          this.conversations.set(conversations);
          this.loading.set(false);
        },

        error: error => {

          console.error(
            'Erreur lors du chargement des conversations :',
            error
          );

          this.loading.set(false);

          this.errorMessage.set(
            'Impossible de charger les conversations.'
          );
        }
      });
  }

  selectConversation(
    conversationId: number
  ): void {

    this.selectedConversationId.set(
      conversationId
    );

    this.conversationSelected.emit(
      conversationId
    );
  }
}