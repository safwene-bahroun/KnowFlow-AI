package tn.knowflowai.backend.Service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.knowflowai.backend.DTO.FlaskRagResponse;
import tn.knowflowai.backend.DTO.RagChatResponse;
import tn.knowflowai.backend.Entity.ChatConversation;
import tn.knowflowai.backend.Entity.Enum.MessageRole;
import tn.knowflowai.backend.Entity.Message;
import tn.knowflowai.backend.Entity.User;


@Service
@Transactional
public class RagChatService {

    private final ChatConversationService
            conversationService;

    private final MessageService
            messageService;

    private final FlaskRagClient
            flaskRagClient;


    public RagChatService(

            ChatConversationService
                    conversationService,

            MessageService
                    messageService,

            FlaskRagClient
                    flaskRagClient
    ) {

        this.conversationService =
                conversationService;

        this.messageService =
                messageService;

        this.flaskRagClient =
                flaskRagClient;
    }


    public RagChatResponse askQuestion(

            User user,

            Long conversationId,

            String question,

            List<Long> allowedDocumentIds
    ) {


        if (
                question == null ||
                question.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Question cannot be empty"
            );
        }


        ChatConversation conversation;


        /*
         * CREATE NEW CONVERSATION
         */

        if (conversationId == null) {

            conversation =
                    new ChatConversation(
                            createTitle(question),
                            user
                    );

            conversation =
                    conversationService.create(
                            conversation
                    );

        }

        /*
         * EXISTING CONVERSATION
         */

        else {

            conversation =
                    conversationService
                            .getByIdAndUser(
                                    conversationId,
                                    user.getId()
                            );
        }


        /*
         * SAVE USER MESSAGE
         */

        Message userMessage =
                new Message(
                        question.trim(),
                        MessageRole.USER,
                        conversation
                );

        messageService.create(
                userMessage
        );


        /*
         * NO DOCUMENT ACCESS
         */

        if (
                allowedDocumentIds == null ||
                allowedDocumentIds.isEmpty()
        ) {

            String answer =
                    "You currently do not have access "
                    + "to any documents. Please contact "
                    + "the administrator to request "
                    + "document access.";


            Message aiMessage =
                    new Message(
                            answer,
                            MessageRole.AI,
                            conversation
                    );


            messageService.create(
                    aiMessage
            );


            return new RagChatResponse(
                    conversation.getId(),
                    answer,
                    Collections.emptyList()
            );
        }


        /*
         * CALL FLASK
         */

        FlaskRagResponse flaskResponse =
                flaskRagClient.ask(
                        question.trim(),
                        allowedDocumentIds
                );


        /*
         * SAVE AI MESSAGE
         */

        Message aiMessage =
                new Message(
                        flaskResponse.getAnswer(),
                        MessageRole.AI,
                        conversation
                );


        messageService.create(
                aiMessage
        );


        /*
         * RETURN RESPONSE TO ANGULAR
         */

        return new RagChatResponse(

                conversation.getId(),

                flaskResponse.getAnswer(),

                flaskResponse.getSourceDocumentIds()
        );
    }


    private String createTitle(
            String question
    ) {

        String cleanQuestion =
                question.trim();


        if (
                cleanQuestion.length() <= 50
        ) {

            return cleanQuestion;
        }


        return cleanQuestion.substring(
                0,
                50
        ) + "...";
    }
}