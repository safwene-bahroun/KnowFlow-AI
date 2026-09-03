package tn.knowflowai.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tn.knowflowai.backend.Entity.ChatConversation;

public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, Long> {


    @Query("""
            SELECT conversation
            FROM ChatConversation conversation
            WHERE conversation.user.id = :userId
            ORDER BY conversation.modifiedAt DESC, conversation.createdAt DESC
            """)
    List<ChatConversation> findAllByUserId(@Param("userId") Long userId);


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