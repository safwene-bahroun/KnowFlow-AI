package tn.knowflowai.backend.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlaskRagResponse {

    private String answer;

    @JsonProperty("source_document_ids")
    private List<Long> sourceDocumentIds;


    public FlaskRagResponse() {
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