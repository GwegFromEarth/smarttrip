import {
  ComponentFixture,
  TestBed
} from '@angular/core/testing';

import { ConversationList } from './conversation-list';

import {
  ConversationService
} from '../conversation.service';

import {
  Conversation
} from '../conversation.models';

import {
  of,
  throwError
} from 'rxjs';

describe('ConversationList', () => {

  let component: ConversationList;
  let fixture: ComponentFixture<ConversationList>;

  let conversationService: {
    getConversations: ReturnType<typeof vi.fn>;
    getMessages: ReturnType<typeof vi.fn>;
  };

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

  beforeEach(async () => {

    conversationService = {
      getConversations: vi.fn(),
      getMessages: vi.fn()
    };

    conversationService.getConversations
      .mockReturnValue(of(conversations));

    await TestBed.configureTestingModule({

      imports: [
        ConversationList
      ],

      providers: [
        {
          provide: ConversationService,
          useValue: conversationService
        }
      ]

    }).compileComponents();

    fixture =
      TestBed.createComponent(ConversationList);

    component =
      fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should be created', () => {

    expect(component)
      .toBeTruthy();

  });

  it('should load conversations on initialization', () => {

    expect(
      conversationService.getConversations
    ).toHaveBeenCalledTimes(1);

    expect(
      component.conversations()
    ).toEqual(conversations);

  });

  it('should display conversations', () => {

    const element =
      fixture.nativeElement as HTMLElement;

    const items =
      element.querySelectorAll(
        '.conversation-item'
      );

    expect(items.length)
      .toBe(2);

    expect(items[0].textContent)
      .toContain('Conversation #2');

    expect(items[1].textContent)
      .toContain('Conversation #1');

  });

  it('should select a conversation', () => {

    component.selectConversation(2);

    expect(
      component.selectedConversationId()
    ).toBe(2);

  });

  it('should mark the selected conversation in the DOM', () => {

    component.selectConversation(2);

    fixture.detectChanges();

    const selected =
      fixture.nativeElement.querySelector(
        '.conversation-item.selected'
      );

    expect(selected)
      .toBeTruthy();

    expect(selected.textContent)
      .toContain('Conversation #2');

  });

  it('should display an error when loading fails', () => {

    conversationService.getConversations
      .mockReturnValueOnce(
        throwError(() => new Error('Erreur réseau'))
      );

    component.loadConversations();

    fixture.detectChanges();

    expect(
      component.errorMessage()
    ).toBe(
      'Impossible de charger les conversations.'
    );

    const error =
      fixture.nativeElement.querySelector(
        '.conversation-error'
      );

    expect(error)
      .toBeTruthy();

  });

  it('should emit the selected conversation id', () => {

    const conversationId = 42;

    let emittedId: number | undefined;

    component.conversationSelected.subscribe(
        id => emittedId = id
    );

    component.selectConversation(conversationId);

    expect(component.selectedConversationId())
        .toBe(conversationId);

    expect(emittedId)
        .toBe(conversationId);
    });

});