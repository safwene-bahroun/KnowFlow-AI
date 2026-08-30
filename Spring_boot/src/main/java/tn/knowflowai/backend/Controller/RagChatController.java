package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.DTO.ChatQuestionRequest;
import tn.knowflowai.backend.DTO.RagChatResponse;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.UserRepository;
import tn.knowflowai.backend.Service.DocumentService;
import tn.knowflowai.backend.Service.RagChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class RagChatController {

    private final RagChatService ragChatService;
    private final UserRepository userRepository;
    private final DocumentService documentService;

    public RagChatController(
            RagChatService ragChatService,
            UserRepository userRepository,
            DocumentService documentService
    ) {
        this.ragChatService = ragChatService;
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    @PostMapping("/ask")
    public ResponseEntity<RagChatResponse> ask(
            @RequestBody ChatQuestionRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new RuntimeException("User is not authenticated");
        }

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Dynamically compute all documents the user is authorized to access
        List<Document> accessibleDocs = documentService.getAccessibleDocuments(user.getEmail());
        List<Long> allowedDocumentIds = accessibleDocs.stream()
                .map(Document::getId)
                .toList();

        System.out.println("User " + user.getEmail() + " has " + allowedDocumentIds.size() + " authorized document(s): " + allowedDocumentIds);

        RagChatResponse response = ragChatService.askQuestion(
                user,
                request.getConversationId(),
                request.getQuestion(),
                allowedDocumentIds
        );

        return ResponseEntity.ok(response);
    }
}