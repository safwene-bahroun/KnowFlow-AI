package tn.knowflowai.backend.DTO;

import java.time.LocalDateTime;

import tn.knowflowai.backend.Entity.Notification;
import tn.knowflowai.backend.Entity.Enum.NotificationType;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean read,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getModifiedAt()
        );
    }
}
