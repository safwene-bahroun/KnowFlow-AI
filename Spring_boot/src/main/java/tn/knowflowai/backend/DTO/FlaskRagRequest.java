package tn.knowflowai.backend.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlaskRagRequest {

    private String question;

    @JsonProperty("allowed_document_ids")
    private List<Long> allowedDocumentIds;


    public FlaskRagRequest() {
    }


    public FlaskRagRequest(
            String question,
            List<Long> allowedDocumentIds
    ) {

        this.question = question;
        this.allowedDocumentIds = allowedDocumentIds;
    }


    public String getQuestion() {
        return question;
    }


    public void setQuestion(String question) {
        this.question = question;
    }


    public List<Long> getAllowedDocumentIds() {
        return allowedDocumentIds;
    }


    public void setAllowedDocumentIds(
            List<Long> allowedDocumentIds
    ) {

        this.allowedDocumentIds = allowedDocumentIds;
    }
}