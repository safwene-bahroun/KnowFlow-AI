import {
  Component,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  ChatBotService,
  ChatConversation
} from '../../../Services/chat-bot-service';

import {
  Subscription
} from 'rxjs';


@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.html',
  styleUrl: './history.css'
})
export class History implements OnInit, OnDestroy {


  // ==========================================
  // OUTPUT EVENTS
  // ==========================================
  @Output()
  conversationSelected = new EventEmitter<ChatConversation>();

  @Output()
  newChatRequested = new EventEmitter<void>();


  // ==========================================
  // CONVERSATIONS
  // ==========================================
  conversations: ChatConversation[] = [];


  // ==========================================
  // STATE
  // ==========================================
  selectedConversationId: number | null = null;
  loading = false;
  errorMessage = '';


  // ==========================================
  // SUBSCRIPTIONS
  // ==========================================
  private subscriptions = new Subscription();


  constructor(
    private chatBotService: ChatBotService
  ) {}


  // ==========================================
  // INITIALIZATION
  // ==========================================
  ngOnInit(): void {
    this.loadConversations();
  }


  // ==========================================
  // DESTROY
  // ==========================================
  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }


  // ==========================================
  // LOAD CONVERSATIONS
  // ==========================================
  loadConversations(): void {
    this.loading = true;
    this.errorMessage = '';

    const sub = this.chatBotService
      .getConversations()
      .subscribe({
        next: (conversations) => {
          this.conversations = conversations;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading conversations', error);
          this.errorMessage = 'Failed to load conversations.';
          this.loading = false;
        }
      });

    this.subscriptions.add(sub);
  }


  // ==========================================
  // SELECT CONVERSATION
  // ==========================================
  selectConversation(conversation: ChatConversation): void {
    if (this.selectedConversationId === conversation.id) {
      return;
    }

    this.selectedConversationId = conversation.id;
    this.errorMessage = '';

    this.conversationSelected.emit(conversation);
  }


  // ==========================================
  // DELETE CONVERSATION
  // ==========================================
  deleteConversation(
    conversation: ChatConversation,
    event: MouseEvent
  ): void {
    event.stopPropagation();

    const confirmed = confirm(`Delete "${conversation.title}"?`);

    if (!confirmed) {
      return;
    }

    const sub = this.chatBotService
      .deleteConversation(conversation.id)
      .subscribe({
        next: () => {
          this.conversations = this.conversations.filter(
            c => c.id !== conversation.id
          );

          if (this.selectedConversationId === conversation.id) {
            this.selectedConversationId = null;
            this.newChatRequested.emit();
          }
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Unable to delete conversation.';
        }
      });

    this.subscriptions.add(sub);
  }

}