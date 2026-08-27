package tn.knowflowai.backend.DTO;

public class ChatQuestionRequest {

    private Long conversationId;

    private String question;


    public ChatQuestionRequest() {
    }


    public Long getConversationId() {
        return conversationId;
    }


    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }


    public String getQuestion() {
        return question;
    }


    public void setQuestion(String question) {
        this.question = question;
    }
}