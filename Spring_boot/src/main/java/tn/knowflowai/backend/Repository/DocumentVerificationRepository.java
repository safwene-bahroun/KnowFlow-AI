package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.DocumentVerification;
import tn.knowflowai.backend.Entity.Enum.VerificationStatus;

import java.util.List;
import java.util.Optional;

public interface DocumentVerificationRepository
        extends JpaRepository<DocumentVerification, Long> {

    Optional<DocumentVerification> findByDocumentId(
            Long documentId
    );

    List<DocumentVerification> findByStatus(
            VerificationStatus status
    );

    List<DocumentVerification> findByFraudDetectedTrue();

    List<DocumentVerification> findByConfidenceScoreGreaterThanEqual(
            Double score
    );

    boolean existsByDocumentId(Long documentId);
}