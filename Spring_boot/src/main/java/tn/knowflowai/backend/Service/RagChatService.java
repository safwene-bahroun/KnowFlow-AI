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

    private final ChatConversationService conversationService;
    private final MessageService messageService;
    private final FlaskRagClient flaskRagClient;

    public RagChatService(
            ChatConversationService conversationService,
            MessageService messageService,
            FlaskRagClient flaskRagClient
    ) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.flaskRagClient = flaskRagClient;
    }

    public RagChatResponse askQuestion(
            User user,
            Long conversationId,
            String question,
            List<Long> allowedDocumentIds
    ) {

        /*
         * Validate question
         */
        if (
                question == null ||
                question.trim().isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "Question cannot be empty"
            );
        }

        /*
         * If null, convert to empty list.
         *
         * [] means:
         * "User has no authorized documents."
         *
         * This does NOT mean:
         * "User cannot use the AI."
         */
        if (allowedDocumentIds == null) {
            allowedDocumentIds =
                    Collections.emptyList();
        }

        /*
         * CREATE OR GET CONVERSATION
         */
        ChatConversation conversation;

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

        } else {

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
         * CALL FLASK ALWAYS
         *
         * If documents exist:
         *
         * [1, 2, 5] → RAG
         *
         * If no documents:
         *
         * [] → normal LLM
         */
        System.out.println(
                "===================================="
        );

        System.out.println(
                "CALLING FLASK RAG/LLM"
        );

        System.out.println(
                "User: " + user.getEmail()
        );

        System.out.println(
                "Question: " + question.trim()
        );

        System.out.println(
                "Authorized documents: "
                        + allowedDocumentIds
        );

        if (allowedDocumentIds.isEmpty()) {

            System.out.println(
                    "MODE: NORMAL LLM"
            );

        } else {

            System.out.println(
                    "MODE: RAG"
            );
        }

        System.out.println(
                "===================================="
        );

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
         * RETURN TO ANGULAR
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

        if (cleanQuestion.length() <= 50) {
            return cleanQuestion;
        }

        return cleanQuestion.substring(
                0,
                50
        ) + "...";
    }
}