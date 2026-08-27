package tn.knowflowai.backend.DTO;

import tn.knowflowai.backend.Entity.Enum.DocumentStatus;

public class UpdateDocumentStatusRequest {

    private DocumentStatus status;

    public UpdateDocumentStatusRequest() {
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }
}