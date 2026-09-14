import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Conversation,
  ConversationMessage
} from './conversation.models';

@Injectable({
  providedIn: 'root'
})
export class ConversationService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8080/api/conversations';

  getConversations(): Observable<Conversation[]> {

    return this.http.get<Conversation[]>(
      this.apiUrl
    );
  }

  getMessages(
    conversationId: number
  ): Observable<ConversationMessage[]> {

    return this.http.get<ConversationMessage[]>(
      `${this.apiUrl}/${conversationId}/messages`
    );
  }
}