package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.DTO.ChatQuestionRequest;
import tn.knowflowai.backend.DTO.RagChatResponse;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.UserRepository;
import tn.knowflowai.backend.Service.RagChatService;


@RestController
@RequestMapping("/api/chat")
public class RagChatController {

    private final RagChatService ragChatService;

    private final UserRepository userRepository;


    public RagChatController(

            RagChatService ragChatService,

            UserRepository userRepository
    ) {

        this.ragChatService =
                ragChatService;

        this.userRepository =
                userRepository;
    }


 @PostMapping("/ask")
public ResponseEntity<RagChatResponse> ask(
        @RequestBody ChatQuestionRequest request,
        Authentication authentication
) {

    System.out.println("====================================");
    System.out.println("CHAT /ASK ENDPOINT CALLED");
    System.out.println("Question: " + request.getQuestion());
    System.out.println("Conversation ID: " + request.getConversationId());
    System.out.println("Authentication: " + authentication);
    System.out.println("====================================");

    if (authentication == null) {
        throw new RuntimeException(
                "User is not authenticated"
        );
    }

    User user =
            userRepository
                    .findByEmail(authentication.getName())
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "User not found"
                            )
                    );

    System.out.println("Authenticated user: " + user.getEmail());

    List<Long> allowedDocumentIds = List.of();

    RagChatResponse response =
            ragChatService.askQuestion(
                    user,
                    request.getConversationId(),
                    request.getQuestion(),
                    allowedDocumentIds
            );

    return ResponseEntity.ok(response);

    }
}