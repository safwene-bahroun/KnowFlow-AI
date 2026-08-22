package tn.knowflowai.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.knowflowai.backend.Entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByNameIgnoreCase(String name);

    List<Department> findByNameContainingIgnoreCase(String name);

    List<Department> findByDescriptionContainingIgnoreCase(
            String keyword
    );

    boolean existsByNameIgnoreCase(String name);
}