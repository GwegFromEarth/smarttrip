import {
  TestBed
} from '@angular/core/testing';

import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { provideHttpClient } from '@angular/common/http';

import { ConversationService } from './conversation.service';

import {
  Conversation,
  ConversationMessage
} from './conversation.models';

describe('ConversationService', () => {

  let service: ConversationService;
  let httpTesting: HttpTestingController;

  const apiUrl =
    'http://localhost:8080/api/conversations';

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        ConversationService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service =
      TestBed.inject(ConversationService);

    httpTesting =
      TestBed.inject(HttpTestingController);
  });

  afterEach(() => {

    httpTesting.verify();

  });

  it('should be created', () => {

    expect(service).toBeTruthy();

  });

  it('should get conversations', () => {

    const conversations: Conversation[] = [
      {
        id: 2,
        createdAt: '2026-09-14T10:00:00',
        updatedAt: '2026-09-14T12:00:00'
      },
      {
        id: 1,
        createdAt: '2026-09-13T10:00:00',
        updatedAt: '2026-09-13T11:00:00'
      }
    ];

    service
      .getConversations()
      .subscribe(result => {

        expect(result)
          .toEqual(conversations);

      });

    const request =
      httpTesting.expectOne(apiUrl);

    expect(request.request.method)
      .toBe('GET');

    request.flush(conversations);
  });

  it('should get messages for a conversation', () => {

    const conversationId = 42;

    const messages: ConversationMessage[] = [
      {
        role: 'user',
        content: 'Que visiter à Rome ?',
        createdAt: '2026-09-14T10:00:00'
      },
      {
        role: 'assistant',
        content: 'Le Colisée est incontournable.',
        createdAt: '2026-09-14T10:01:00'
      }
    ];

    service
      .getMessages(conversationId)
      .subscribe(result => {

        expect(result)
          .toEqual(messages);

      });

    const request =
      httpTesting.expectOne(
        `${apiUrl}/${conversationId}/messages`
      );

    expect(request.request.method)
      .toBe('GET');

    request.flush(messages);
  });

});