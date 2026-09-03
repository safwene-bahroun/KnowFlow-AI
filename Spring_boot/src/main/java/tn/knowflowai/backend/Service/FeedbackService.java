package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.Feedback;
import tn.knowflowai.backend.Entity.Enum.FeedbackType;
import tn.knowflowai.backend.Repository.FeedbackRepository;

import java.util.List;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository repository;
    private final NotificationService notificationService;

    public FeedbackService(
            FeedbackRepository repository,
            NotificationService notificationService
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public Feedback create(Feedback feedback) {
        Feedback saved = repository.save(feedback);
        notificationService.notifyFeedbackReceived(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Feedback> getAll() {
        return repository.findAllWithContext();
    }

    @Transactional(readOnly = true)
    public Feedback getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Feedback not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Feedback> getByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> getByDocument(
            Long documentId
    ) {
        return repository.findByDocumentId(documentId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> getByMessage(
            Long messageId
    ) {
        return repository.findByMessageId(messageId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> getByType(
            FeedbackType type
    ) {
        return repository.findByType(type);
    }

    public Feedback update(
            Long id,
            Feedback updated
    ) {

        Feedback feedback = getById(id);

        feedback.setType(updated.getType());
        feedback.setComment(updated.getComment());

        Feedback saved = repository.save(feedback);
        notificationService.notifyFeedbackReceived(saved);
        return saved;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}