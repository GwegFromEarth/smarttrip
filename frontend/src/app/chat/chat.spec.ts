import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Chat } from './chat';
import { ChatService } from './chat.service';

describe('Chat', () => {

  let component: Chat;
  let fixture: ComponentFixture<Chat>;

  beforeEach(async () => {

    const chatService = {
      streamChat: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Chat],
      providers: [
        {
          provide: ChatService,
          useValue: chatService
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

});