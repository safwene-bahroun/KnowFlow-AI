import { Injectable } from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';


export interface ChatRequest {

  conversationId: number | null;

  question: string;

}


export interface ChatResponse {

  conversationId: number;

  answer: string;

  sourceDocumentIds: number[];

}


export interface ChatConversation {

  id: number;

  title: string;

}


export interface ChatMessage {

  id?: number;

  content: string;

  role: 'USER' | 'AI';

}


@Injectable({
  providedIn: 'root'
})

export class ChatBotService {

  private readonly apiUrl =
    'http://localhost:3000/api/chat';


  constructor(
    private http: HttpClient
  ) {}


  // ==========================================
  // ASK QUESTION
  // ==========================================

  askQuestion(

    question: string,

    conversationId: number | null

  ): Observable<ChatResponse> {

    const request: ChatRequest = {

      conversationId: conversationId,

      question: question

    };


    return this.http.post<ChatResponse>(

      `${this.apiUrl}/ask`,

      request

    );

  }


  // ==========================================
  // GET USER CONVERSATIONS
  // ==========================================

  getConversations(): Observable<ChatConversation[]> {

    return this.http.get<ChatConversation[]>(

      `${this.apiUrl}/conversations`

    );

  }


  // ==========================================
  // GET CONVERSATION MESSAGES
  // ==========================================

  getMessages(

    conversationId: number

  ): Observable<ChatMessage[]> {

    return this.http.get<ChatMessage[]>(

      `${this.apiUrl}/conversations/${conversationId}/messages`

    );

  }


  // ==========================================
  // DELETE CONVERSATION
  // ==========================================

  deleteConversation(

    conversationId: number

  ): Observable<void> {

    return this.http.delete<void>(

      `${this.apiUrl}/conversations/${conversationId}`

    );

  }

}