package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.Feedback;
import tn.knowflowai.backend.Entity.Enum.FeedbackType;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {

        @Query("SELECT f FROM Feedback f JOIN FETCH f.user LEFT JOIN FETCH f.document LEFT JOIN FETCH f.message ORDER BY f.createdAt DESC")
        List<Feedback> findAllWithContext();

    List<Feedback> findByUserId(Long userId);

    List<Feedback> findByDocumentId(Long documentId);

    List<Feedback> findByMessageId(Long messageId);

    List<Feedback> findByType(FeedbackType type);

    List<Feedback> findByDocumentIdAndType(
            Long documentId,
            FeedbackType type
    );

    List<Feedback> findByMessageIdAndType(
            Long messageId,
            FeedbackType type
    );
}