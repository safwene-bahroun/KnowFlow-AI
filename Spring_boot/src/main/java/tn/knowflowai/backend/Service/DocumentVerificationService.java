package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.DocumentVerification;
import tn.knowflowai.backend.Entity.Enum.VerificationStatus;
import tn.knowflowai.backend.Repository.DocumentVerificationRepository;

import java.util.List;

@Service
@Transactional
public class DocumentVerificationService {

    private final DocumentVerificationRepository repository;

    public DocumentVerificationService(
            DocumentVerificationRepository repository
    ) {
        this.repository = repository;
    }

    public DocumentVerification create(
            DocumentVerification verification
    ) {
        return repository.save(verification);
    }

    @Transactional(readOnly = true)
    public List<DocumentVerification> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public DocumentVerification getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Verification not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public DocumentVerification getByDocument(
            Long documentId
    ) {
        return repository.findByDocumentId(documentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Verification not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<DocumentVerification> getByStatus(
            VerificationStatus status
    ) {
        return repository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<DocumentVerification> getFraudulent() {
        return repository.findByFraudDetectedTrue();
    }

    public DocumentVerification update(
            Long id,
            DocumentVerification updated
    ) {

        DocumentVerification verification = getById(id);

        verification.setStatus(updated.getStatus());
        verification.setFraudDetected(
                updated.isFraudDetected()
        );
        verification.setConfidenceScore(
                updated.getConfidenceScore()
        );
        verification.setAnalysisResult(
                updated.getAnalysisResult()
        );
        verification.setVerificationModel(
                updated.getVerificationModel()
        );

        return repository.save(verification);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}