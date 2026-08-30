package tn.knowflowai.backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentIngestRequest {

    @JsonProperty("document_id")
    private Long documentId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("author")
    private String author;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("created_by_id")
    private Long createdById;

    public DocumentIngestRequest() {}

    public DocumentIngestRequest(
            Long documentId,
            String name,
            String url,
            String mimeType,
            String description,
            String author,
            String visibility,
            Long departmentId,
            Long createdById
    ) {
        this.documentId = documentId;
        this.name = name;
        this.url = url;
        this.mimeType = mimeType;
        this.description = description;
        this.author = author;
        this.visibility = visibility;
        this.departmentId = departmentId;
        this.createdById = createdById;
    }

    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
}
