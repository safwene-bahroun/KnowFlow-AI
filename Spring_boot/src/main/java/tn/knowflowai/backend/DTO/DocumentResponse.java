package tn.knowflowai.backend.DTO;

import java.time.LocalDateTime;
import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Entity.Document;
import tn.knowflowai.backend.Entity.Enum.DocumentStatus;
import tn.knowflowai.backend.Entity.Enum.DocumentVisibility;
import tn.knowflowai.backend.Entity.User;

public record DocumentResponse(
        Long id,
        String name,
        String url,
        String mimeType,
        Long fileSize,
        String description,
        String author,
        DocumentStatus status,
        DocumentVisibility visibility,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        CreatedByResponse createdBy,
        DepartmentResponse department
) {

    public static DocumentResponse from(Document document) {
        User user = document.getCreatedBy();
        Department department = document.getDepartment();

        return new DocumentResponse(
                document.getId(),
                document.getName(),
                document.getUrl(),
                document.getMimeType(),
                document.getFileSize(),
                document.getDescription(),
                document.getAuthor(),
                document.getStatus(),
                document.getVisibility(),
                document.getCreatedAt(),
                document.getModifiedAt(),
                user == null ? null : new CreatedByResponse(
                        user.getId(), user.getName(), user.getFamilyName(), user.getEmail()),
                department == null ? null : new DepartmentResponse(
                        department.getId(), department.getName())
        );
    }

    public record CreatedByResponse(
            Long id,
            String name,
            String familyName,
            String email
    ) {}

    public record DepartmentResponse(Long id, String name) {}
}
