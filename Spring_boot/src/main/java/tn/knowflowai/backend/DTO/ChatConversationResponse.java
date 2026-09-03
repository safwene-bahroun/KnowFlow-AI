package tn.knowflowai.backend.DTO;

import tn.knowflowai.backend.Entity.ChatConversation;

public record ChatConversationResponse(Long id, String title) {

    public static ChatConversationResponse from(ChatConversation conversation) {
        return new ChatConversationResponse(conversation.getId(), conversation.getTitle());
    }
}