package tn.knowflowai.backend.Entity;

import jakarta.persistence.*;
import tn.knowflowai.backend.Entity.Enum.VerificationStatus;

@Entity
@Table(name = "document_verifications")
public class DocumentVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationStatus status;

    @Column(name = "fraud_detected", nullable = false)
    private boolean fraudDetected = false;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private String analysisResult;

    @Column(name = "verification_model", length = 100)
    private String verificationModel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    public DocumentVerification() {
    }

    public DocumentVerification(
            VerificationStatus status,
            boolean fraudDetected,
            Double confidenceScore,
            String analysisResult,
            String verificationModel,
            Document document
    ) {
        this.status = status;
        this.fraudDetected = fraudDetected;
        this.confidenceScore = confidenceScore;
        this.analysisResult = analysisResult;
        this.verificationModel = verificationModel;
        this.document = document;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public boolean isFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }

    public String getVerificationModel() {
        return verificationModel;
    }

    public void setVerificationModel(String verificationModel) {
        this.verificationModel = verificationModel;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}