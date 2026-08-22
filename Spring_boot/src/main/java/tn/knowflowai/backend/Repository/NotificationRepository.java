package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.Notification;
import tn.knowflowai.backend.Entity.Enum.NotificationType;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Notification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
            Long userId
    );

    List<Notification> findByRecipientIdAndType(
            Long userId,
            NotificationType type
    );

    long countByRecipientIdAndReadFalse(
            Long userId
    );
}