package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Service.DocumentService;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeDocumentController {

    private final DocumentService documentService;

    public EmployeeDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<Document> getAccessibleDocuments(Authentication authentication) {
        return documentService.getAccessibleDocuments(authentication.getName());
    }

    @GetMapping("/{id}")
    public Document getAccessibleDocument(
            @PathVariable Long id,
            Authentication authentication) {
        return documentService.getAccessibleById(id, authentication.getName());
    }
}