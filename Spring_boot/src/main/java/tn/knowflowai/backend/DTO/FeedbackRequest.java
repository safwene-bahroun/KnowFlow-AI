package tn.knowflowai.backend.DTO;

import tn.knowflowai.backend.Entity.Enum.FeedbackType;

public record FeedbackRequest(FeedbackType type, String comment, Long documentId, Long messageId) {
}