package tn.knowflowai.backend.DTO;

import java.util.List;

public class RagChatResponse {

    private Long conversationId;

    private String answer;

    private List<Long> sourceDocumentIds;


    public RagChatResponse() {
    }


    public RagChatResponse(
            Long conversationId,
            String answer,
            List<Long> sourceDocumentIds
    ) {

        this.conversationId = conversationId;
        this.answer = answer;
        this.sourceDocumentIds = sourceDocumentIds;
    }


    public Long getConversationId() {
        return conversationId;
    }


    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }


    public String getAnswer() {
        return answer;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public List<Long> getSourceDocumentIds() {
        return sourceDocumentIds;
    }


    public void setSourceDocumentIds(
            List<Long> sourceDocumentIds
    ) {

        this.sourceDocumentIds = sourceDocumentIds;
    }
}