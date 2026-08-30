package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Service.DepartmentService;

@RestController
@RequestMapping({"/api/admin/departments", "/api/departments"})
@CrossOrigin(origins = "http://localhost:4200")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // CREATE (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> create(
            @RequestBody Department department) {

        Department createdDepartment =
                departmentService.create(department);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDepartment);
    }

    // GET ALL (All authenticated users)
    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(
                departmentService.getAll()
        );
    }

    // GET BY ID (All authenticated users)
    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getById(id)
        );
    }

    // GET BY NAME
    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                departmentService.getByName(name)
        );
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Department>> search(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                departmentService.search(keyword)
        );
    }

    // UPDATE (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> update(
            @PathVariable Long id,
            @RequestBody Department department) {

        return ResponseEntity.ok(
                departmentService.update(id, department)
        );
    }

    // DELETE (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        departmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}