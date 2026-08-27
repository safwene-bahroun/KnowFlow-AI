package tn.knowflowai.backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tn.knowflowai.backend.Entity.Enum.MessageRole;

@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;


    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 10
    )
    private MessageRole role;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "conversation_id",
            nullable = false
    )
    private ChatConversation conversation;


    public Message() {
    }


    public Message(
            String content,
            MessageRole role,
            ChatConversation conversation
    ) {

        this.content = content;
        this.role = role;
        this.conversation = conversation;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public MessageRole getRole() {
        return role;
    }


    public void setRole(MessageRole role) {
        this.role = role;
    }


    public ChatConversation getConversation() {
        return conversation;
    }


    public void setConversation(
            ChatConversation conversation
    ) {

        this.conversation = conversation;
    }
}