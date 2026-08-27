package tn.knowflowai.backend.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.DTO.DocumentRequest;
import tn.knowflowai.backend.DTO.DocumentResponse;
import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.DepartmentRepository;
import tn.knowflowai.backend.Repository.DocumentRepository;
import tn.knowflowai.backend.Repository.UserRepository;
import tn.knowflowai.backend.Service.DocumentService;
import tn.knowflowai.backend.Service.FileStorageService;

@RestController
@RequestMapping("/api/admin/documents")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final DocumentService documentService;
        private final DocumentRepository documentRepository;
        private final DepartmentRepository departmentRepository;
        private final UserRepository userRepository;
        private final FileStorageService fileStorageService;

        public DocumentController(
                        DocumentService documentService,
                        DocumentRepository documentRepository,
                        DepartmentRepository departmentRepository,
                        UserRepository userRepository,
                        FileStorageService fileStorageService) {
        this.documentService = documentService;
                this.documentRepository = documentRepository;
                this.departmentRepository = departmentRepository;
                this.userRepository = userRepository;
                this.fileStorageService = fileStorageService;
    }

    // ==========================================
    // CREATE
    // POST /api/documents
    // ==========================================

    @PostMapping(
                        consumes = "application/json")
    public ResponseEntity<Document> createDocument(
                        @org.springframework.web.bind.annotation.RequestBody DocumentRequest request,
            Authentication authentication) throws IOException {

        Document document = new Document();
                document.setName(request.getName());
                document.setDescription(request.getDescription());
                document.setAuthor(request.getAuthor());
                document.setStatus(request.getStatus());
                document.setVisibility(request.getVisibility());
                document.setDepartment(resolveDepartment(request.getDepartmentId()));
        document.setCreatedBy(findUser(authentication));
                byte[] fileData = decodeFile(request.getFileData());
                document.setUrl(fileStorageService.store(fileData, request.getFileName()));
                document.setMimeType(request.getMimeType());
                document.setFileSize(request.getFileSize() != null ? request.getFileSize() : (long) fileData.length);
        normalizeVisibility(document);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentRepository.save(document));
    }

    // ==========================================
    // GET ALL
    // GET /api/documents
    // ==========================================

    @GetMapping
        public ResponseEntity<List<DocumentResponse>> getAll() {

        return ResponseEntity.ok(
                responses(documentService.getAll())
        );
    }

    // ==========================================
    // GET BY ID
    // GET /api/documents/{id}
    // ==========================================

    @GetMapping("/{id}")
        public ResponseEntity<DocumentResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                DocumentResponse.from(documentService.getById(id))
        );
    }

    // ==========================================
    // SEARCH
    // GET /api/documents/search?keyword=...
    // ==========================================

    @GetMapping("/search")
        public ResponseEntity<List<DocumentResponse>> search(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                responses(documentService.search(keyword))
        );
    }

    // ==========================================
    // GET BY NAME
    // GET /api/documents/name/{name}
    // ==========================================

    @GetMapping("/name/{name}")
        public ResponseEntity<List<DocumentResponse>> getByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                responses(documentService.getByName(name))
        );
    }

    // ==========================================
    // GET BY DEPARTMENT
    // GET /api/documents/department/{departmentId}
    // ==========================================

    @GetMapping("/department/{departmentId}")
        public ResponseEntity<List<DocumentResponse>>
    getDocumentsByDepartment(
            @PathVariable Long departmentId) {

        return ResponseEntity.ok(
                responses(documentService
                        .getDocumentsByDepartment(departmentId))
        );
    }

    // ==========================================
    // GET BY STATUS
    // GET /api/documents/status/{status}
    // ==========================================

    @GetMapping("/status/{status}")
        public ResponseEntity<List<DocumentResponse>> getByStatus(
            @PathVariable DocumentStatus status) {

        return ResponseEntity.ok(
                responses(documentService.getByStatus(status))
        );
    }

    // ==========================================
    // GET BY VISIBILITY
    // GET /api/documents/visibility/{visibility}
    // ==========================================

    @GetMapping("/visibility/{visibility}")
        public ResponseEntity<List<DocumentResponse>> getByVisibility(
            @PathVariable DocumentVisibility visibility) {

        return ResponseEntity.ok(
                responses(documentService.getByVisibility(visibility))
        );
    }

    // ==========================================
    // UPDATE
    // PUT /api/documents/{id}
    // ==========================================

                @PutMapping(value = "/{id}", consumes = "application/json")
        public ResponseEntity<Document> updateDocument(
            @PathVariable Long id,
                                                @org.springframework.web.bind.annotation.RequestBody DocumentRequest request)
                        throws IOException {

                Document document = documentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Document not found"));
                document.setName(request.getName());
                document.setDescription(request.getDescription());
                document.setAuthor(request.getAuthor());
                document.setStatus(request.getStatus());
                document.setVisibility(request.getVisibility());
                document.setDepartment(resolveDepartment(request.getDepartmentId()));
                normalizeVisibility(document);

                if (request.getFileData() != null && !request.getFileData().isBlank()) {
                        byte[] fileData = decodeFile(request.getFileData());
                        document.setUrl(fileStorageService.store(fileData, request.getFileName()));
                        document.setMimeType(request.getMimeType());
                        document.setFileSize(request.getFileSize() != null ? request.getFileSize() : (long) fileData.length);
                }

                return ResponseEntity.ok(documentRepository.save(document));
    }

        private List<DocumentResponse> responses(List<Document> documents) {
                return documents.stream()
                                .map(DocumentResponse::from)
                                .toList();
        }

        private Department resolveDepartment(Long departmentId) {
                if (departmentId == null) {
                        return null;
                }
                return departmentRepository.findById(departmentId)
                                .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        private User findUser(Authentication authentication) {
                return userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException("User not found"));
        }

        private byte[] decodeFile(String fileData) {
                if (fileData == null || fileData.isBlank()) {
                        throw new IllegalArgumentException("File is required");
                }
                return Base64.getDecoder().decode(fileData);
        }

        private void normalizeVisibility(Document document) {
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

    // ==========================================
    // UPDATE STATUS
    // PATCH /api/documents/{id}/status
    // ==========================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<Document> updateStatus(
            @PathVariable Long id,
            @RequestParam DocumentStatus status) {

        return ResponseEntity.ok(
                documentService.updateStatus(
                        id,
                        status
                )
        );
    }

    // ==========================================
    // UPDATE VISIBILITY
    // PATCH /api/documents/{id}/visibility
    // ==========================================

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<Document> updateVisibility(
            @PathVariable Long id,
            @RequestParam DocumentVisibility visibility,
            @RequestParam(required = false) Long departmentId) {

        return ResponseEntity.ok(
                documentService.updateVisibility(
                        id,
                        visibility,
                        departmentId
                )
        );
    }

    // ==========================================
    // DELETE
    // DELETE /api/documents/{id}
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        documentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}