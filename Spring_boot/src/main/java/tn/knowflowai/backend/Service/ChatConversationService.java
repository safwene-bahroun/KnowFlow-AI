package tn.knowflowai.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.knowflowai.backend.Entity.ChatConversation;
import tn.knowflowai.backend.Repository.ChatConversationRepository;


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

        return repository.save(
                conversation
        );
    }


    @Transactional(readOnly = true)
    public List<ChatConversation>
    getByUser(Long userId) {

        return repository
                .findAllByUserId(
                        userId
                );
    }


    @Transactional(readOnly = true)
    public ChatConversation
    getByIdAndUser(
            Long conversationId,
            Long userId
    ) {

        return repository

                .findByIdAndUserId(
                        conversationId,
                        userId
                )

                .orElseThrow(
                        () -> new RuntimeException(
                                "Conversation not found"
                        )
                );
    }


    public ChatConversation update(
            Long conversationId,
            Long userId,
            String title
    ) {

        ChatConversation conversation =
                getByIdAndUser(
                        conversationId,
                        userId
                );

        conversation.setTitle(title);

        return repository.save(
                conversation
        );
    }


    public void delete(
            Long conversationId,
            Long userId
    ) {

        ChatConversation conversation =
                getByIdAndUser(
                        conversationId,
                        userId
                );

        repository.delete(
                conversation
        );
    }
}