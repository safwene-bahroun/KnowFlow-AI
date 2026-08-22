package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.Notification;
import tn.knowflowai.backend.Entity.Enum.NotificationType;
import tn.knowflowai.backend.Repository.NotificationRepository;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(
            NotificationRepository repository
    ) {
        this.repository = repository;
    }

    public Notification create(Notification notification) {
        return repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Notification getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notification not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Notification> getByUser(
            Long userId
    ) {
        return repository
                .findByRecipientIdOrderByCreatedAtDesc(
                        userId
                );
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnread(
            Long userId
    ) {
        return repository
                .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
                        userId
                );
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return repository.countByRecipientIdAndReadFalse(
                userId
        );
    }

    public Notification markAsRead(Long id) {

        Notification notification = getById(id);

        notification.setRead(true);

        return repository.save(notification);
    }

    public Notification update(
            Long id,
            Notification updated
    ) {

        Notification notification = getById(id);

        notification.setTitle(updated.getTitle());
        notification.setMessage(updated.getMessage());
        notification.setType(updated.getType());
        notification.setRead(updated.isRead());

        return repository.save(notification);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}