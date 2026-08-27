package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.DTO.ChunkRequest;
import tn.knowflowai.backend.DTO.SaveChunksRequest;
import tn.knowflowai.backend.DTO.UpdateDocumentStatusRequest;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.DocumentChunk;
import tn.knowflowai.backend.Service.DocumentChunkService;
import tn.knowflowai.backend.Service.DocumentService;


@RestController
@RequestMapping("/api/internal/documents")
public class InternalDocumentController {

    private final DocumentService documentService;

    private final DocumentChunkService documentChunkService;
private final String internalApiKey;

   public InternalDocumentController(

        DocumentService documentService,

        DocumentChunkService documentChunkService,
        @Value("${internal.api.key}")
        String internalApiKey
) {

    this.documentService =
            documentService;

    this.documentChunkService =
            documentChunkService;

    this.internalApiKey =
            internalApiKey;
}

    /*
     * =====================================================
     * SAVE DOCUMENT CHUNKS
     * =====================================================
     *
     * POST
     *
     * /api/internal/documents/{documentId}/chunks
     */

    @PostMapping("/{documentId}/chunks")
    public ResponseEntity<?> saveChunks(

            @PathVariable Long documentId,

            @RequestBody SaveChunksRequest request,

            @RequestHeader(
                    "X-Internal-API-Key"
            ) String apiKey
    ) {

        validateInternalKey(
                apiKey
        );

        Document document =
                documentService.getById(
                        documentId
                );

        List<DocumentChunk> chunks =
                request.getChunks()
                        .stream()
                        .map(
                                chunkRequest ->
                                        convertToEntity(
                                                chunkRequest,
                                                document
                                        )
                        )
                        .toList();

        documentChunkService
                .replaceDocumentChunks(
                        document,
                        chunks
                );

        return ResponseEntity.ok(
                java.util.Map.of(

                        "message",
                        "Chunks saved successfully",

                        "documentId",
                        documentId,

                        "chunkCount",
                        chunks.size()
                )
        );
    }


    /*
     * =====================================================
     * UPDATE DOCUMENT STATUS
     * =====================================================
     *
     * PUT
     *
     * /api/internal/documents/{documentId}/status
     */

    @PutMapping("/{documentId}/status")
    public ResponseEntity<?> updateStatus(

            @PathVariable Long documentId,

            @RequestBody
            UpdateDocumentStatusRequest request,

            @RequestHeader(
                    "X-Internal-API-Key"
            ) String apiKey
    ) {

        validateInternalKey(
                apiKey
        );

        Document document =
                documentService.updateStatus(
                        documentId,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                java.util.Map.of(

                        "message",
                        "Document status updated successfully",

                        "documentId",
                        document.getId(),

                        "status",
                        document.getStatus()
                                .name()
                )
        );
    }


    /*
     * =====================================================
     * CONVERT DTO TO ENTITY
     * =====================================================
     */

    private DocumentChunk convertToEntity(

            ChunkRequest request,

            Document document
    ) {

        return new DocumentChunk(

                request.getChunkIndex(),

                request.getContent(),

                request.getTokenCount(),

                document
        );
    }


    /*
     * =====================================================
     * INTERNAL API SECURITY
     * =====================================================
     */
private void validateInternalKey(
        String apiKey
) {

    if (
            apiKey == null ||
            !internalApiKey.equals(
                    apiKey
            )
    ) {

        throw new SecurityException(
                "Invalid internal API key"
        );
    }
}
}