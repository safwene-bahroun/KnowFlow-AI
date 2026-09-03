package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.Notification;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Entity.Enum.NotificationType;
import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Repository.NotificationRepository;
import tn.knowflowai.backend.Repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository repository;
        private final UserRepository userRepository;

    public NotificationService(
                        NotificationRepository repository,
                        UserRepository userRepository
    ) {
        this.repository = repository;
                this.userRepository = userRepository;
    }

    public Notification create(Notification notification) {
        return repository.save(notification);
    }

    public void notifyDocumentChange(Document document, boolean updated) {
        NotificationType type = updated
                ? NotificationType.DOCUMENT_UPDATED
                : NotificationType.DOCUMENT_UPLOADED;
        String action = updated ? "updated" : "added";

        userRepository.findAll().stream()
                .filter(user -> user.isEnabled() &&
                        (user.getRole() == Role.EMPLOYEE || user.getRole() == Role.MANAGER))
                .filter(user -> canView(document, user))
                .forEach(user -> create(new Notification(
                        "Document " + action,
                        "The document '" + document.getName() + "' was " + action + ".",
                        type,
                        user)));
    }

    public void notifyFeedbackReceived(tn.knowflowai.backend.Entity.Feedback feedback) {
        if (feedback.getUser() == null ||
                (feedback.getUser().getRole() != Role.EMPLOYEE &&
                        feedback.getUser().getRole() != Role.MANAGER)) {
            return;
        }

        String subject = feedback.getDocument() != null
                ? " for document '" + feedback.getDocument().getName() + "'"
                : "";
        userRepository.findByRole(Role.ADMIN).stream()
                .filter(User::isEnabled)
                .forEach(admin -> create(new Notification(
                        "New feedback received",
                        "A " + feedback.getUser().getRole().name().toLowerCase()
                                + " submitted feedback" + subject + ".",
                        NotificationType.FEEDBACK_RECEIVED,
                        admin)));
    }

    private boolean canView(Document document, User user) {
        if (document.getVisibility() == null) {
            return false;
        }
        return switch (document.getVisibility()) {
            case DEPARTMENT -> document.getDepartment() != null &&
                    user.getDepartment() != null &&
                    document.getDepartment().getId().equals(user.getDepartment().getId());
            case COMPANY, PUBLIC -> true;
            case MANAGERS_ONLY -> user.getRole() == Role.MANAGER;
            case PRIVATE -> document.getCreatedBy() != null &&
                    document.getCreatedBy().getId().equals(user.getId());
        };
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

        public void markFeedbackNotificationsRead(Long userId) {
                repository.findByRecipientIdAndTypeAndReadFalse(
                                userId, NotificationType.FEEDBACK_RECEIVED
                ).forEach(notification -> notification.setRead(true));
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