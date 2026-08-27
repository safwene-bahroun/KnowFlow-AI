package tn.knowflowai.backend.DTO;

import java.util.List;

public class RagQueryRequest {

    private String question;

    private Long conversationId;

    private List<Long> allowedDocumentIds;


    public RagQueryRequest() {
    }


    public String getQuestion() {
        return question;
    }


    public void setQuestion(
            String question
    ) {
        this.question = question;
    }


    public Long getConversationId() {
        return conversationId;
    }


    public void setConversationId(
            Long conversationId
    ) {
        this.conversationId =
                conversationId;
    }


    public List<Long> getAllowedDocumentIds() {
        return allowedDocumentIds;
    }


    public void setAllowedDocumentIds(
            List<Long> allowedDocumentIds
    ) {
        this.allowedDocumentIds =
                allowedDocumentIds;
    }
}