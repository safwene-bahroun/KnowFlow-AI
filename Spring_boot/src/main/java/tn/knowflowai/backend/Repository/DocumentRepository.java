package tn.knowflowai.backend.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @EntityGraph(attributePaths = {"createdBy", "department"})
    @Query("SELECT d FROM Document d")
    List<Document> findAllWithRelations();

    @EntityGraph(attributePaths = {"createdBy", "department"})
    @Query("SELECT d FROM Document d WHERE d.id = :id")
    Optional<Document> findByIdWithRelations(Long id);

    @EntityGraph(attributePaths = {"createdBy", "department"})
    List<Document> findByNameContainingIgnoreCase(String name);

    List<Document> findByMimeTypeIgnoreCase(String mimeType);

    @EntityGraph(attributePaths = {"createdBy", "department"})
    List<Document> findByStatus(DocumentStatus status);

    @EntityGraph(attributePaths = {"createdBy", "department"})
    List<Document> findByVisibility(
            DocumentVisibility visibility
    );

    @EntityGraph(attributePaths = {"createdBy", "department"})
    List<Document> findByDepartmentId(Long departmentId);

    @EntityGraph(attributePaths = {"createdBy", "department"})
    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.name) LIKE
              LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(d.description) LIKE
              LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(d.author) LIKE
              LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(d.url) LIKE
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