import {
  Component,
  OnInit
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


@Component({

  selector: 'app-chat-bot',

  standalone: true,

  imports: [

    CommonModule,

    FormsModule

  ],

  templateUrl: './chat-bot.html',

  styleUrl: './chat-bot.css'

})

export class ChatBot implements OnInit {


  // ==========================================
  // CURRENT CONVERSATION
  // ==========================================

  conversationId:

    number | null = null;


  // ==========================================
  // CONVERSATIONS
  // ==========================================

  conversations:

    ChatConversation[] = [];


  // ==========================================
  // MESSAGES
  // ==========================================

  messages:

    ChatMessage[] = [];


  // ==========================================
  // INPUT
  // ==========================================

  question = '';


  loading = false;


  errorMessage = '';


  constructor(

    private chatBotService:
      ChatBotService

  ) {}


  // ==========================================
  // INITIALIZATION
  // ==========================================

  ngOnInit(): void {

    this.loadConversations();

  }


  // ==========================================
  // LOAD CONVERSATIONS
  // ==========================================

  loadConversations(): void {

    this.chatBotService

      .getConversations()

      .subscribe({

        next: (

          conversations

        ) => {

          this.conversations =
            conversations;

        },

        error: (

          error

        ) => {

          console.error(

            'Error loading conversations',

            error

          );

        }

      });

  }


  // ==========================================
  // NEW CHAT
  // ==========================================

  newConversation(): void {

    this.conversationId =
      null;

    this.messages =
      [];

    this.question =
      '';

    this.errorMessage =
      '';

  }


  // ==========================================
  // SELECT CONVERSATION
  // ==========================================

  selectConversation(

    conversation:
      ChatConversation

  ): void {

    this.conversationId =
      conversation.id;


    this.loading =
      true;


    this.errorMessage =
      '';


    this.chatBotService

      .getMessages(

        conversation.id

      )

      .subscribe({

        next: (

          messages

        ) => {

          this.messages =
            messages;

          this.loading =
            false;

        },

        error: (

          error

        ) => {

          console.error(

            error

          );


          this.errorMessage =
            'Unable to load conversation.';


          this.loading =
            false;

        }

      });

  }


  // ==========================================
  // SEND MESSAGE
  // ==========================================

  sendMessage(): void {

    const question =
      this.question.trim();


    if (

      !question ||

      this.loading

    ) {

      return;

    }


    this.errorMessage =
      '';


    // Add user message immediately

    this.messages.push({

      content:
        question,

      role:
        'USER'

    });


    this.question =
      '';


    this.loading =
      true;


    this.chatBotService

      .askQuestion(

        question,

        this.conversationId

      )

      .subscribe({

        next: (

          response

        ) => {


          // Store conversation ID

          this.conversationId =
            response.conversationId;


          // Add AI answer

          this.messages.push({

            content:
              response.answer,

            role:
              'AI'

          });


          this.loading =
            false;


          // Reload sidebar

          this.loadConversations();

        },

error: (error) => {

  console.error(
    '========== CHAT ERROR =========='
  );

  console.error(
    'Status:',
    error.status
  );

  console.error(
    'Status Text:',
    error.statusText
  );

  console.error(
    'URL:',
    error.url
  );

  console.error(
    'Error:',
    error.error
  );

  console.error(
    'Full error:',
    error
  );

  console.error(
    '================================'
  );

  this.errorMessage =
    `Chat error (${error.status}): ${
      error?.error?.message ||
      error?.message ||
      'Unknown error'
    }`;

  this.loading = false;
}
      });

  }


  // ==========================================
  // ENTER KEY
  // ==========================================

  onEnter(

    event:
      KeyboardEvent

  ): void {

    if (

      event.key === 'Enter' &&

      !event.shiftKey

    ) {

      event.preventDefault();


      this.sendMessage();

    }

  }


  // ==========================================
  // DELETE CONVERSATION
  // ==========================================

  deleteConversation(

    conversation:
      ChatConversation,

    event:
      MouseEvent

  ): void {

    event.stopPropagation();


    const confirmed =

      confirm(

        `Delete "${conversation.title}"?`

      );


    if (

      !confirmed

    ) {

      return;

    }


    this.chatBotService

      .deleteConversation(

        conversation.id

      )

      .subscribe({

        next: () => {


          // If deleted conversation is open

          if (

            this.conversationId ===
            conversation.id

          ) {

            this.newConversation();

          }


          this.loadConversations();

        },


        error: (

          error

        ) => {

          console.error(

            error

          );


          this.errorMessage =
            'Unable to delete conversation.';

        }

      });

  }

}