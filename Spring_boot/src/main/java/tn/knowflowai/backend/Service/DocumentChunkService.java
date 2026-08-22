package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.DocumentChunk;
import tn.knowflowai.backend.Repository.DocumentChunkRepository;

import java.util.List;

@Service
@Transactional
public class DocumentChunkService {

    private final DocumentChunkRepository chunkRepository;

    public DocumentChunkService(
            DocumentChunkRepository chunkRepository
    ) {
        this.chunkRepository = chunkRepository;
    }

    public DocumentChunk create(DocumentChunk chunk) {
        return chunkRepository.save(chunk);
    }

    @Transactional(readOnly = true)
    public List<DocumentChunk> getAll() {
        return chunkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DocumentChunk getById(Long id) {
        return chunkRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document chunk not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<DocumentChunk> getByDocument(
            Long documentId
    ) {
        return chunkRepository
                .findByDocumentIdOrderByChunkIndexAsc(
                        documentId
                );
    }

    @Transactional(readOnly = true)
    public List<DocumentChunk> searchContent(
            String keyword
    ) {
        return chunkRepository
                .findByContentContainingIgnoreCase(keyword);
    }

    public DocumentChunk update(
            Long id,
            DocumentChunk updatedChunk
    ) {

        DocumentChunk chunk = getById(id);

        chunk.setContent(updatedChunk.getContent());
        chunk.setChunkIndex(updatedChunk.getChunkIndex());
        chunk.setTokenCount(updatedChunk.getTokenCount());

        return chunkRepository.save(chunk);
    }

    public void delete(Long id) {
        chunkRepository.deleteById(id);
    }
}