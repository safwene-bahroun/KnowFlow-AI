package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.ChatConversation;
import tn.knowflowai.backend.Repository.ChatConversationRepository;

import java.util.List;

@Service
@Transactional
public class ChatConversationService {

    private final ChatConversationRepository repository;

    public ChatConversationService(
            ChatConversationRepository repository
    ) {
        this.repository = repository;
    }

    public ChatConversation create(
            ChatConversation conversation
    ) {
        return repository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<ChatConversation> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ChatConversation getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Conversation not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<ChatConversation> getByUser(
            Long userId
    ) {
        return repository
                .findByUserIdOrderByModifiedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<ChatConversation> searchByTitle(
            Long userId,
            String title
    ) {
        return repository
                .findByTitleContainingIgnoreCaseAndUserId(
                        title,
                        userId
                );
    }

    public ChatConversation update(
            Long id,
            ChatConversation updated
    ) {

        ChatConversation conversation = getById(id);

        conversation.setTitle(updated.getTitle());

        return repository.save(conversation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}