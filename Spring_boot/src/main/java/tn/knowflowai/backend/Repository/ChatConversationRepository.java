package tn.knowflowai.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.knowflowai.backend.Entity.ChatConversation;

public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, Long> {


    List<ChatConversation>
    findByUserIdOrderByModifiedAtDesc(
            Long userId
    );


    Optional<ChatConversation>
    findByIdAndUserId(
            Long conversationId,
            Long userId
    );


    List<ChatConversation>
    findByTitleContainingIgnoreCaseAndUserId(
            String title,
            Long userId
    );


    long countByUserId(
            Long userId
    );
}