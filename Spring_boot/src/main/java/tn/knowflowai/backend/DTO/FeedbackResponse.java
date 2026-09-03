package tn.knowflowai.backend.DTO;

import tn.knowflowai.backend.Entity.Feedback;

public record FeedbackResponse(
        Long id,
        String type,
        String comment,
        String userName,
        String userEmail,
        String documentName,
        String messageContent,
        String createdAt
) {
    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getType().name(),
                feedback.getComment(),
                feedback.getUser().getName() + " " + feedback.getUser().getFamilyName(),
                feedback.getUser().getEmail(),
                feedback.getDocument() == null ? null : feedback.getDocument().getName(),
                feedback.getMessage() == null ? null : feedback.getMessage().getContent(),
                feedback.getCreatedAt() == null ? null : feedback.getCreatedAt().toString()
        );
    }
}