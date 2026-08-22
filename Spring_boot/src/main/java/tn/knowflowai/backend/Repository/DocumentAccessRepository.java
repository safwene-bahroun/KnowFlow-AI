package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.DocumentAccess;

import java.util.List;
import java.util.Optional;

public interface DocumentAccessRepository
        extends JpaRepository<DocumentAccess, Long> {

    List<DocumentAccess> findByDocumentId(Long documentId);

    List<DocumentAccess> findByUserId(Long userId);

    Optional<DocumentAccess> findByDocumentIdAndUserId(
            Long documentId,
            Long userId
    );

    boolean existsByDocumentIdAndUserId(
            Long documentId,
            Long userId
    );

    List<DocumentAccess> findByUserIdAndCanReadTrue(
            Long userId
    );

    List<DocumentAccess> findByUserIdAndCanDownloadTrue(
            Long userId
    );
}