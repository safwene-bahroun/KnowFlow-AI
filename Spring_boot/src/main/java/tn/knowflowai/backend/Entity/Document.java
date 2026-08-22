package tn.knowflowai.backend.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;

@Entity
@Table(name = "documents")
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(length = 500)
    private String description;

    /*
     * The person who wrote/created the document.
     * This is just a name/text, not a User relationship.
     */
    @Column(length = 255)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentVisibility visibility;

    /*
     * Department to which the document belongs.
     *
     * Used especially when visibility = DEPARTMENT.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("chunkIndex ASC")
    private List<DocumentChunk> chunks = new ArrayList<>();

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocumentAccess> accessList = new ArrayList<>();

    @OneToOne(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private DocumentVerification verification;

    @OneToMany(mappedBy = "document")
    private List<Feedback> feedbacks = new ArrayList<>();


    public Document() {
    }


    public Document(
            String name,
            String url,
            String mimeType,
            Long fileSize,
            String description,
            String author,
            DocumentStatus status,
            DocumentVisibility visibility,
            Department department
    ) {
        this.name = name;
        this.url = url;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.description = description;
        this.author = author;
        this.status = status;
        this.visibility = visibility;
        this.department = department;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }


    public DocumentVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(DocumentVisibility visibility) {
        this.visibility = visibility;
    }


    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }


    public List<DocumentChunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<DocumentChunk> chunks) {
        this.chunks = chunks;
    }


    public List<DocumentAccess> getAccessList() {
        return accessList;
    }

    public void setAccessList(List<DocumentAccess> accessList) {
        this.accessList = accessList;
    }


    public DocumentVerification getVerification() {
        return verification;
    }

    public void setVerification(DocumentVerification verification) {
        this.verification = verification;
    }


    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}