package tn.knowflowai.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Repository.DepartmentRepository;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(
            DepartmentRepository departmentRepository
    ) {
        this.departmentRepository = departmentRepository;
    }

    // CREATE
    public Department create(Department department) {

        if (departmentRepository
                .existsByNameIgnoreCase(department.getName())) {

            throw new RuntimeException(
                    "Department already exists"
            );
        }

        return departmentRepository.save(department);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public Department getById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found"
                        )
                );
    }

    // GET BY NAME
    @Transactional(readOnly = true)
    public Department getByName(String name) {
        return departmentRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found"
                        )
                );
    }

    // SEARCH
    @Transactional(readOnly = true)
    public List<Department> search(String keyword) {
        return departmentRepository
                .findByNameContainingIgnoreCase(keyword);
    }

    // UPDATE
    public Department update(
            Long id,
            Department updatedDepartment
    ) {

        Department department = getById(id);

        department.setName(updatedDepartment.getName());
        department.setDescription(
                updatedDepartment.getDescription()
        );

        return departmentRepository.save(department);
    }

    // DELETE
    public void delete(Long id) {

        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException(
                    "Department not found"
            );
        }

        departmentRepository.deleteById(id);
    }
}