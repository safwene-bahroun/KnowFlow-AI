package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.DocumentAccess;
import tn.knowflowai.backend.Repository.DocumentAccessRepository;

import java.util.List;

@Service
@Transactional
public class DocumentAccessService {

    private final DocumentAccessRepository accessRepository;

    public DocumentAccessService(
            DocumentAccessRepository accessRepository
    ) {
        this.accessRepository = accessRepository;
    }

    public DocumentAccess create(DocumentAccess access) {
        return accessRepository.save(access);
    }

    @Transactional(readOnly = true)
    public List<DocumentAccess> getAll() {
        return accessRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DocumentAccess getById(Long id) {
        return accessRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document access not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<DocumentAccess> getByDocument(
            Long documentId
    ) {
        return accessRepository.findByDocumentId(documentId);
    }

    @Transactional(readOnly = true)
    public List<DocumentAccess> getByUser(Long userId) {
        return accessRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public DocumentAccess getByDocumentAndUser(
            Long documentId,
            Long userId
    ) {
        return accessRepository
                .findByDocumentIdAndUserId(
                        documentId,
                        userId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Access not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public boolean canRead(
            Long documentId,
            Long userId
    ) {
        return accessRepository
                .findByDocumentIdAndUserId(
                        documentId,
                        userId
                )
                .map(DocumentAccess::isCanRead)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canDownload(
            Long documentId,
            Long userId
    ) {
        return accessRepository
                .findByDocumentIdAndUserId(
                        documentId,
                        userId
                )
                .map(DocumentAccess::isCanDownload)
                .orElse(false);
    }

    public DocumentAccess update(
            Long id,
            DocumentAccess updated
    ) {

        DocumentAccess access = getById(id);

        access.setCanRead(updated.isCanRead());
        access.setCanDownload(updated.isCanDownload());
        access.setCanEdit(updated.isCanEdit());
        access.setCanDelete(updated.isCanDelete());

        return accessRepository.save(access);
    }

    public void delete(Long id) {
        accessRepository.deleteById(id);
    }
}