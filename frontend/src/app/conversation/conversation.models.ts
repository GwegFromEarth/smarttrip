export interface Conversation {
  id: number;
  createdAt: string;
  updatedAt: string;
}

export interface ConversationMessage {
  role: 'user' | 'assistant';
  content: string;
  createdAt: string;
}