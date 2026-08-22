package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;
import tn.knowflowai.backend.Service.DocumentService;

@RestController
@RequestMapping("/api/admin/documents")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // ==========================================
    // CREATE
    // POST /api/documents
    // ==========================================

    @PostMapping
    public ResponseEntity<Document> create(
            @RequestBody Document document) {

        Document createdDocument =
                documentService.create(document);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDocument);
    }

    // ==========================================
    // GET ALL
    // GET /api/documents
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Document>> getAll() {

        return ResponseEntity.ok(
                documentService.getAll()
        );
    }

    // ==========================================
    // GET BY ID
    // GET /api/documents/{id}
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                documentService.getById(id)
        );
    }

    // ==========================================
    // SEARCH
    // GET /api/documents/search?keyword=...
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<List<Document>> search(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                documentService.search(keyword)
        );
    }

    // ==========================================
    // GET BY NAME
    // GET /api/documents/name/{name}
    // ==========================================

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Document>> getByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                documentService.getByName(name)
        );
    }

    // ==========================================
    // GET BY DEPARTMENT
    // GET /api/documents/department/{departmentId}
    // ==========================================

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Document>>
    getDocumentsByDepartment(
            @PathVariable Long departmentId) {

        return ResponseEntity.ok(
                documentService
                        .getDocumentsByDepartment(departmentId)
        );
    }

    // ==========================================
    // GET BY STATUS
    // GET /api/documents/status/{status}
    // ==========================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Document>> getByStatus(
            @PathVariable DocumentStatus status) {

        return ResponseEntity.ok(
                documentService.getByStatus(status)
        );
    }

    // ==========================================
    // GET BY VISIBILITY
    // GET /api/documents/visibility/{visibility}
    // ==========================================

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<Document>> getByVisibility(
            @PathVariable DocumentVisibility visibility) {

        return ResponseEntity.ok(
                documentService.getByVisibility(visibility)
        );
    }

    // ==========================================
    // UPDATE
    // PUT /api/documents/{id}
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<Document> update(
            @PathVariable Long id,
            @RequestBody Document document) {

        return ResponseEntity.ok(
                documentService.update(id, document)
        );
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
            @RequestParam DocumentVisibility visibility) {

        return ResponseEntity.ok(
                documentService.updateVisibility(
                        id,
                        visibility
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