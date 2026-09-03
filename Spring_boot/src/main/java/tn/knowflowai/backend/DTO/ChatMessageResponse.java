package tn.knowflowai.backend.DTO;

import tn.knowflowai.backend.Entity.Message;

public record ChatMessageResponse(Long id, String content, String role) {

    public static ChatMessageResponse from(Message message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getContent(),
                message.getRole().name()
        );
    }
}