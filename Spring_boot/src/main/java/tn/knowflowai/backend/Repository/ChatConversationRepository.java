package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.ChatConversation;

import java.util.List;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    List<ChatConversation> findByUserIdOrderByModifiedAtDesc(
            Long userId
    );

    List<ChatConversation>
    findByTitleContainingIgnoreCaseAndUserId(
            String title,
            Long userId
    );

    long countByUserId(Long userId);
}