package tn.knowflowai.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;
import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.DepartmentRepository;
import tn.knowflowai.backend.Repository.DocumentRepository;
import tn.knowflowai.backend.Repository.UserRepository;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository
    ) {
        this.documentRepository = documentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    public Document create(Document document, String currentUserEmail) {
        document.setCreatedBy(findUser(currentUserEmail));
        document.setDepartment(resolveDepartment(document.getDepartment()));
        applyVisibilityRules(document);
        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<Document> getAccessibleDocuments(String currentUserEmail) {
        User user = findUser(currentUserEmail);

        return documentRepository.findAll().stream()
                .filter(document -> canView(document, user))
                .toList();
    }

    @Transactional(readOnly = true)
    public Document getAccessibleById(Long id, String currentUserEmail) {
        Document document = getById(id);
        User user = findUser(currentUserEmail);

        if (!canView(document, user)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You do not have access to this document"
            );
        }

        return document;
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<Document> getAll() {
        return documentRepository.findAllWithRelations();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public Document getById(Long id) {
        return documentRepository.findByIdWithRelations(id)
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
        document.setDepartment(resolveDepartment(updatedDocument.getDepartment()));
        applyVisibilityRules(document);

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
            DocumentVisibility visibility,
            Long departmentId
    ) {

        Document document = getById(id);

        document.setVisibility(visibility);
        document.setDepartment(resolveDepartment(departmentId));
        applyVisibilityRules(document);

        return documentRepository.save(document);
    }

    private boolean canView(Document document, User user) {
        if (!user.isEnabled() || user.getRole() == null) {
            return false;
        }

        if (user.getRole() == Role.ADMIN ||
                (document.getCreatedBy() != null &&
                        document.getCreatedBy().getId().equals(user.getId()))) {
            return true;
        }

        return switch (document.getVisibility()) {
            case PRIVATE -> false;
            case DEPARTMENT -> document.getDepartment() != null &&
                    user.getDepartment() != null &&
                    document.getDepartment().getId().equals(user.getDepartment().getId());
            case COMPANY -> user.getRole() == Role.EMPLOYEE ||
                    user.getRole() == Role.MANAGER;
            case MANAGERS_ONLY -> user.getRole() == Role.MANAGER;
            case PUBLIC -> user.getRole() == Role.EMPLOYEE ||
                    user.getRole() == Role.MANAGER;
        };
    }

    private void applyVisibilityRules(Document document) {
        if (document.getVisibility() == null) {
            throw new IllegalArgumentException("Visibility is required");
        }

        if (document.getVisibility() == DocumentVisibility.DEPARTMENT &&
            document.getDepartment() == null) {
            document.setVisibility(DocumentVisibility.PRIVATE);
        }

        if (document.getVisibility() != DocumentVisibility.DEPARTMENT) {
            document.setDepartment(null);
        }
    }

    private Department resolveDepartment(Department department) {
        return department == null ? null : resolveDepartment(department.getId());
    }

    private Department resolveDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }

        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
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