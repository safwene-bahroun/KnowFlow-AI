import {
  ChangeDetectorRef,
  Component,
  OnInit,
  OnDestroy,
  inject
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  ChatBotService,
  ChatConversation,
  ChatMessage
} from '../../../Services/chat-bot-service';

import {
  AuthService,
  UserResponse
} from '../../../Services/auth-service';
import { AiAvatar, AiAvatarState } from '../../../shared/ai-avatar/ai-avatar';


@Component({
  selector: 'app-chat-bot',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AiAvatar
  ],
  templateUrl: './chat-bot.html',
  styleUrl: './chat-bot.css'
})
export class ChatBot implements OnInit, OnDestroy {
  get avatarState(): AiAvatarState {
    if (this.showTyping) return 'processing';
    return this.messages.length ? 'success' : 'idle';
  }


  // ==========================================
  // CURRENT CONVERSATION
  // ==========================================
  conversationId: number | null = null;


  // ==========================================
  // CONVERSATIONS
  // ==========================================
  conversations: ChatConversation[] = [];
  historyOpen = false;
  conversationsLoading = false;
  conversationsError = '';


  // ==========================================
  // MESSAGES
  // ==========================================
  messages: ChatMessage[] = [];


  // ==========================================
  // INPUT
  // ==========================================
  question = '';
  loading = false;
  errorMessage = '';


  // ==========================================
  // TYPING INDICATOR
  // ==========================================
  showTyping = false;


  // ==========================================
  // USER INFO (from AuthService)
  // ==========================================
  userImage: string = '';
  userInitial: string = 'U';
  currentUser: UserResponse | null = null;


  private readonly chatBotService = inject(ChatBotService);
  private readonly authService    = inject(AuthService);
  private readonly cdr             = inject(ChangeDetectorRef);


  // ==========================================
  // INITIALIZATION
  // ==========================================
  ngOnInit(): void {
    this.loadConversations();
    this.loadUserInfo();
  }


  // ==========================================
  // DESTROY
  // ==========================================
  ngOnDestroy(): void {
    this.showTyping = false;
  }


  // ==========================================
  // LOAD USER INFO
  // ==========================================
  private loadUserInfo(): void {
    this.currentUser = this.authService.getCurrentUser();

    if (this.currentUser) {
      // Set user image if available
      this.userImage = this.currentUser.urlImage || '';

      // Generate initials from name and familyName
      const name = (this.currentUser.name || '').trim().charAt(0).toUpperCase();
      const familyName = (this.currentUser.familyName || '').trim().charAt(0).toUpperCase();
      this.userInitial = name + familyName || 'U';
    }
  }


  // ==========================================
  // LOAD CONVERSATIONS
  // ==========================================
  loadConversations(): void {
    this.conversationsLoading = true;
    this.conversationsError = '';
    this.chatBotService
      .getConversations()
      .subscribe({
        next: (conversations) => {
          this.conversations = conversations;
          this.conversationsLoading = false;
          this.cdr.markForCheck();
        },
        error: (error) => {
          console.error('Error loading conversations', error);
          this.conversationsLoading = false;
          this.conversationsError = 'Unable to load conversations.';
          this.cdr.markForCheck();
        }
      });
  }


  // ==========================================
  // NEW CHAT
  // ==========================================
  newConversation(): void {
    this.conversationId = null;
    this.messages = [];
    this.question = '';
    this.errorMessage = '';
    this.showTyping = false;
  }

  toggleHistory(): void {
    this.historyOpen = !this.historyOpen;
    if (this.historyOpen) {
      this.loadConversations();
    }
  }


  // ==========================================
  // SELECT CONVERSATION
  // ==========================================
  selectConversation(conversation: ChatConversation): void {
    this.conversationId = conversation.id;
    this.loading = true;
    this.errorMessage = '';
    this.showTyping = false;

    this.chatBotService
      .getMessages(conversation.id)
      .subscribe({
        next: (messages) => {
          this.messages = messages;
          this.loading = false;
          this.cdr.markForCheck();
          this.scrollToBottom();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Unable to load conversation.';
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }


  // ==========================================
  // SEND MESSAGE
  // ==========================================
  sendMessage(): void {
    const question = this.question.trim();

    if (!question || this.loading) {
      return;
    }

    this.errorMessage = '';

    // Add user message immediately
    this.messages.push({
      content: question,
      role: 'USER'
    });

    this.question = '';
    this.loading = true;
    this.showTyping = true;

    // Reset textarea height
    this.resetTextareaHeight();

    // Scroll to bottom
    this.scrollToBottom();

    this.chatBotService
      .askQuestion(question, this.conversationId)
      .subscribe({
        next: (response) => {
          // Hide typing indicator
          this.showTyping = false;

          // Store conversation ID
          this.conversationId = response.conversationId;

          // Add AI answer
          this.messages.push({
            content: response.answer,
            role: 'AI'
          });

          this.loading = false;
          this.cdr.markForCheck();

          // Reload sidebar
          this.loadConversations();

          // Scroll to bottom
          this.scrollToBottom();
        },

        error: (error) => {
          console.error('========== CHAT ERROR ==========');
          console.error('Status:', error.status);
          console.error('Status Text:', error.statusText);
          console.error('URL:', error.url);
          console.error('Error:', error.error);
          console.error('Full error:', error);
          console.error('================================');

          this.showTyping = false;
          this.errorMessage = `Chat error (${error.status}): ${
            error?.error?.message ||
            error?.message ||
            'Unknown error'
          }`;
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }


  // ==========================================
  // ENTER KEY
  // ==========================================
  onEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }


  // ==========================================
  // AUTO-GROW TEXTAREA
  // ==========================================
  autoGrow(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    if (!textarea) return;

    // Reset height to get correct scrollHeight
    textarea.style.height = 'auto';

    // Set new height (max 150px)
    const maxHeight = 150;
    const newHeight = Math.min(textarea.scrollHeight, maxHeight);
    textarea.style.height = `${newHeight}px`;

    // Show/hide scrollbar
    textarea.style.overflowY = textarea.scrollHeight > maxHeight ? 'auto' : 'hidden';
  }


  // ==========================================
  // RESET TEXTAREA HEIGHT
  // ==========================================
  private resetTextareaHeight(): void {
    const textarea = document.querySelector('.chat-input-container textarea') as HTMLTextAreaElement;
    if (textarea) {
      textarea.style.height = 'auto';
      textarea.style.overflowY = 'hidden';
    }
  }


  // ==========================================
  // SCROLL TO BOTTOM
  // ==========================================
  private scrollToBottom(): void {
    setTimeout(() => {
      const container = document.getElementById('messagesContainer');
      if (container) {
        container.scrollTo({
          top: container.scrollHeight,
          behavior: 'smooth'
        });
      }
    }, 50);
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

    this.chatBotService
      .deleteConversation(conversation.id)
      .subscribe({
        next: () => {
          // If deleted conversation is open
          if (this.conversationId === conversation.id) {
            this.newConversation();
          }
          this.loadConversations();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Unable to delete conversation.';
        }
      });
  }

}