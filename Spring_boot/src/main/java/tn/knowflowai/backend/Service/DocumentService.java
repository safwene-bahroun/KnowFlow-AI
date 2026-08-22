package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;
import tn.knowflowai.backend.Repository.DocumentRepository;

import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(
            DocumentRepository documentRepository
    ) {
        this.documentRepository = documentRepository;
    }

    // CREATE
    public Document create(Document document) {
        return documentRepository.save(document);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public Document getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found"
                        )
                );
    }

    // SEARCH
    @Transactional(readOnly = true)
    public List<Document> search(String keyword) {
        return documentRepository.search(keyword);
    }

    // GET BY NAME
    @Transactional(readOnly = true)
    public List<Document> getByName(String name) {
        return documentRepository
                .findByNameContainingIgnoreCase(name);
    }

    // GET BY DEPARTMENT
    @Transactional(readOnly = true)
   public List<Document> getDocumentsByDepartment(Long departmentId) {
    return documentRepository.findByDepartmentId(departmentId);
}
    // GET BY STATUS
    @Transactional(readOnly = true)
    public List<Document> getByStatus(
            DocumentStatus status
    ) {
        return documentRepository.findByStatus(status);
    }

    // GET BY VISIBILITY
    @Transactional(readOnly = true)
    public List<Document> getByVisibility(
            DocumentVisibility visibility
    ) {
        return documentRepository.findByVisibility(visibility);
    }

    // UPDATE
    public Document update(
            Long id,
            Document updatedDocument
    ) {

        Document document = getById(id);

        document.setName(updatedDocument.getName());
        document.setUrl(updatedDocument.getUrl());
        document.setMimeType(updatedDocument.getMimeType());
        document.setFileSize(updatedDocument.getFileSize());
        document.setDescription(
                updatedDocument.getDescription()
        );
        document.setStatus(updatedDocument.getStatus());
        document.setVisibility(
                updatedDocument.getVisibility()
        );

        return documentRepository.save(document);
    }

    // UPDATE STATUS
    public Document updateStatus(
            Long id,
            DocumentStatus status
    ) {

        Document document = getById(id);

        document.setStatus(status);

        return documentRepository.save(document);
    }

    // UPDATE VISIBILITY
    public Document updateVisibility(
            Long id,
            DocumentVisibility visibility
    ) {

        Document document = getById(id);

        document.setVisibility(visibility);

        return documentRepository.save(document);
    }

    // DELETE
    public void delete(Long id) {

        if (!documentRepository.existsById(id)) {
            throw new RuntimeException(
                    "Document not found"
            );
        }

        documentRepository.deleteById(id);
    }
}