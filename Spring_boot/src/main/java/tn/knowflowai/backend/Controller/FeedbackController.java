package tn.knowflowai.backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.Entity.Feedback;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.DTO.FeedbackRequest;
import tn.knowflowai.backend.DTO.FeedbackResponse;
import tn.knowflowai.backend.Repository.DocumentRepository;
import tn.knowflowai.backend.Repository.UserRepository;
import tn.knowflowai.backend.Service.FeedbackService;
import tn.knowflowai.backend.Service.NotificationService;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:4200")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;
        private final DocumentRepository documentRepository;
        private final NotificationService notificationService;

    public FeedbackController(
            FeedbackService feedbackService,
            UserRepository userRepository,
            DocumentRepository documentRepository,
            NotificationService notificationService
    ) {
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> create(
            @RequestBody FeedbackRequest request,
            Authentication authentication
    ) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.EMPLOYEE && user.getRole() != Role.MANAGER) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only employees and managers can submit feedback");
        }
        if (request.type() == null || request.comment() == null || request.comment().isBlank()) {
            throw new IllegalArgumentException("Feedback type and comment are required");
        }
        Feedback feedback = new Feedback();
        feedback.setType(request.type());
        feedback.setComment(request.comment());
        feedback.setUser(user);
        if (request.documentId() != null) {
            Document document = documentRepository.findById(request.documentId())
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            feedback.setDocument(document);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(FeedbackResponse.from(feedbackService.create(feedback)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<FeedbackResponse>> getAll(Authentication authentication) {
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationService.markFeedbackNotificationsRead(admin.getId());
        return ResponseEntity.ok(feedbackService.getAll().stream()
                .map(FeedbackResponse::from)
                .toList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
}