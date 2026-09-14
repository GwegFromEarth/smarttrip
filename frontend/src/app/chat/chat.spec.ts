import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { vi } from 'vitest';

import { Chat } from './chat';
import { ChatService } from './chat.service';
import { ConversationService } from '../conversation/conversation.service';

describe('Chat', () => {

  let component: Chat;
  let fixture: ComponentFixture<Chat>;

  beforeEach(async () => {

    const chatService = {
      streamChat: vi.fn()
    };

    const conversationService = {
      getConversations: vi.fn().mockReturnValue(of([])),
      getMessages: vi.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [Chat],
      providers: [
        {
          provide: ChatService,
          useValue: chatService
        },
        {
          provide: ConversationService,
          useValue: conversationService
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Chat);
    component = fixture.componentInstance;

    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {

    expect(component).toBeTruthy();

  });

  it('should have an empty message initially', () => {

    expect(component.message())
      .toBe('');

  });

  it('should have an empty conversation initially', () => {

    expect(component.messages())
      .toEqual([]);

  });

  it('should not be streaming initially', () => {

    expect(component.isStreaming())
      .toBe(false);

  });

  it('should not have a conversation id initially', () => {

    expect(component.conversationId())
      .toBeNull();

  });

  it('should disable the send button when the message is empty', () => {

    const button =
      fixture.nativeElement.querySelector(
        'button'
      ) as HTMLButtonElement;

    expect(button.disabled)
      .toBe(true);

  });

  it('should add the user message when sending', () => {

    const chatService =
      TestBed.inject(ChatService) as unknown as {
        streamChat: ReturnType<typeof vi.fn>;
      };

    chatService.streamChat.mockReturnValue(
      of(
        {
          type: 'conversation',
          data: '42'
        },
        {
          type: 'content',
          data: 'Bonjour !'
        }
      )
    );

    component.message.set('Bonjour');

    component.sendStreamMessage();

    expect(chatService.streamChat)
      .toHaveBeenCalledWith(
        null,
        'Bonjour'
      );

    expect(component.messages()[0])
      .toEqual({
        role: 'user',
        content: 'Bonjour'
      });

  });

  it('should display the assistant response', () => {

    const chatService =
      TestBed.inject(ChatService) as unknown as {
        streamChat: ReturnType<typeof vi.fn>;
      };

    chatService.streamChat.mockReturnValue(
      of(
        {
          type: 'conversation',
          data: '42'
        },
        {
          type: 'content',
          data: 'Bonjour '
        },
        {
          type: 'content',
          data: '! 👋'
        }
      )
    );

    component.message.set('Bonjour');

    component.sendStreamMessage();

    expect(component.conversationId())
      .toBe(42);

    expect(component.messages())
      .toEqual([
        {
          role: 'user',
          content: 'Bonjour'
        },
        {
          role: 'assistant',
          content: 'Bonjour ! 👋',
          streaming: false
        }
      ]);

    expect(component.isStreaming())
      .toBe(false);

  });

  it('should stop the streaming and keep the partial response', () => {

    const chatService =
      TestBed.inject(ChatService) as unknown as {
        streamChat: ReturnType<typeof vi.fn>;
      };

    const stream$ = new Subject<{
      type: 'conversation' | 'content';
      data: string;
    }>();

    chatService.streamChat.mockReturnValue(stream$.asObservable());

    component.message.set('Parle-moi de Rome');

    component.sendStreamMessage();

    expect(component.isStreaming())
      .toBe(true);

    stream$.next({
      type: 'conversation',
      data: '42'
    });

    stream$.next({
      type: 'content',
      data: 'Rome possède de nombreux monuments.'
    });

    expect(component.messages()[1])
      .toEqual({
        role: 'assistant',
        content: 'Rome possède de nombreux monuments.',
        streaming: true
      });

    component.stopStreaming();

    expect(component.isStreaming())
      .toBe(false);

    expect(component.messages()[1])
      .toEqual({
        role: 'assistant',
        content: 'Rome possède de nombreux monuments.',
        streaming: false
      });

    stream$.next({
      type: 'content',
      data: ' Ce contenu ne doit plus être reçu.'
    });

    expect(component.messages()[1].content)
      .toBe('Rome possède de nombreux monuments.');
  });

  it('should display an error when streaming fails', () => {

    const chatService =
      TestBed.inject(ChatService) as unknown as {
        streamChat: ReturnType<typeof vi.fn>;
      };

    const stream$ = new Subject<{
      type: 'conversation' | 'content';
      data: string;
    }>();

    chatService.streamChat.mockReturnValue(
      stream$.asObservable()
    );

    component.message.set('Que visiter à Rome ?');

    component.sendStreamMessage();

    stream$.next({
      type: 'conversation',
      data: '42'
    });

    stream$.next({
      type: 'content',
      data: 'Le Colisée est un incontournable.'
    });

    stream$.error(
      new Error('Connexion interrompue')
    );

    expect(component.isStreaming())
      .toBe(false);

    expect(component.errorMessage())
      .toBe(
        'Une erreur est survenue pendant la réponse. Veuillez réessayer.'
      );

    expect(component.messages()[1])
      .toEqual({
        role: 'assistant',
        content: 'Le Colisée est un incontournable.',
        streaming: false
      });
  });

  it('should clear the previous error when starting a new message', () => {

    const chatService =
      TestBed.inject(ChatService) as unknown as {
        streamChat: ReturnType<typeof vi.fn>;
      };

    chatService.streamChat.mockReturnValue(
      of({
        type: 'conversation',
        data: '42'
      })
    );

    component.errorMessage.set(
      'Une erreur est survenue pendant la réponse.'
    );

    component.message.set('Nouvelle question');

    component.sendStreamMessage();

    expect(component.errorMessage())
      .toBeNull();
  });

});