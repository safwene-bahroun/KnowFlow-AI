package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.Message;
import tn.knowflowai.backend.Entity.Enum.MessageRole;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );

    List<Message> findByConversationIdAndRole(
            Long conversationId,
            MessageRole role
    );

    List<Message> findByContentContainingIgnoreCase(
            String keyword
    );

    long countByConversationId(Long conversationId);

    long countByRole(MessageRole role);
}