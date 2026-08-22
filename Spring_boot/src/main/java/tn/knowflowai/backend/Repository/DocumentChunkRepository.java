package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.DocumentChunk;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(
            Long documentId
    );

    List<DocumentChunk> findByContentContainingIgnoreCase(
            String keyword
    );

    long countByDocumentId(Long documentId);
}