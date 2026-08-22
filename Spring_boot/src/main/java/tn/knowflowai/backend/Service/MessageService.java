package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.Message;
import tn.knowflowai.backend.Entity.Enum.MessageRole;
import tn.knowflowai.backend.Repository.MessageRepository;

import java.util.List;

@Service
@Transactional
public class MessageService {

    private final MessageRepository repository;

    public MessageService(
            MessageRepository repository
    ) {
        this.repository = repository;
    }

    public Message create(Message message) {
        return repository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Message getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Message not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Message> getByConversation(
            Long conversationId
    ) {
        return repository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                );
    }

    @Transactional(readOnly = true)
    public List<Message> getByRole(
            Long conversationId,
            MessageRole role
    ) {
        return repository.findByConversationIdAndRole(
                conversationId,
                role
        );
    }

    @Transactional(readOnly = true)
    public List<Message> searchContent(
            String keyword
    ) {
        return repository
                .findByContentContainingIgnoreCase(keyword);
    }

    public Message update(
            Long id,
            Message updated
    ) {

        Message message = getById(id);

        message.setContent(updated.getContent());
        message.setRole(updated.getRole());

        return repository.save(message);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}