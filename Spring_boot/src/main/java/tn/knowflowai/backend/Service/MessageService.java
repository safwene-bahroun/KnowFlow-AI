package tn.knowflowai.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.knowflowai.backend.Entity.Enum.MessageRole;
import tn.knowflowai.backend.Entity.Message;
import tn.knowflowai.backend.Repository.MessageRepository;


@Service
@Transactional
public class MessageService {

    private final MessageRepository repository;


    public MessageService(
            MessageRepository repository
    ) {

        this.repository = repository;
    }


    public Message create(
            Message message
    ) {

        return repository.save(message);
    }


    @Transactional(readOnly = true)
    public Message getByIdAndUser(
            Long messageId,
            Long userId
    ) {

        return repository

                .findByIdAndConversationUserId(
                        messageId,
                        userId
                )

                .orElseThrow(
                        () -> new RuntimeException(
                                "Message not found"
                        )
                );
    }


    @Transactional(readOnly = true)
    public List<Message>
    getByConversation(
            Long conversationId
    ) {

        return repository

                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                );
    }


    @Transactional(readOnly = true)
    public List<Message>
    getByRole(
            Long conversationId,
            MessageRole role
    ) {

        return repository
                .findByConversationIdAndRole(
                        conversationId,
                        role
                );
    }


    public void delete(
            Long messageId,
            Long userId
    ) {

        Message message =
                getByIdAndUser(
                        messageId,
                        userId
                );

        repository.delete(message);
    }
}