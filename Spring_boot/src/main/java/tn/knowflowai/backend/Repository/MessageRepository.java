package tn.knowflowai.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.knowflowai.backend.Entity.Enum.MessageRole;
import tn.knowflowai.backend.Entity.Message;

public interface MessageRepository
        extends JpaRepository<Message, Long> {


    Optional<Message>
    findByIdAndConversationUserId(
            Long messageId,
            Long userId
    );


    List<Message>
    findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );


    List<Message>
    findByConversationIdAndRole(
            Long conversationId,
            MessageRole role
    );


    long countByConversationId(
            Long conversationId
    );


    long countByRole(
            MessageRole role
    );
}