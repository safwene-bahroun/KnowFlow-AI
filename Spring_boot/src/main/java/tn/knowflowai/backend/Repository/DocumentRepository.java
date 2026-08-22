package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByNameContainingIgnoreCase(String name);

    List<Document> findByMimeTypeIgnoreCase(String mimeType);

    List<Document> findByStatus(DocumentStatus status);

    List<Document> findByVisibility(
            DocumentVisibility visibility
    );

    List<Document> findByDepartmentId(Long departmentId);

    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.name) LIKE
              LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(d.description) LIKE
              LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Document> search(
            @Param("keyword") String keyword
    );

    @Query("""
        SELECT d FROM Document d
        WHERE d.visibility = :visibility
        AND d.status = :status
        """)
    List<Document> findByVisibilityAndStatus(
            @Param("visibility") DocumentVisibility visibility,
            @Param("status") DocumentStatus status
    );

    long countByStatus(DocumentStatus status);

    long countByVisibility(DocumentVisibility visibility);
}