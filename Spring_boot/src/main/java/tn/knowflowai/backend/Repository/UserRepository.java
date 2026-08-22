package tn.knowflowai.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCin(String cin);

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByFamilyNameContainingIgnoreCase(String familyName);

    List<User> findByNameContainingIgnoreCaseOrFamilyNameContainingIgnoreCase(
            String name,
            String familyName
    );

    List<User> findByRole(Role role);

    List<User> findByDepartmentName(String departmentName);

    List<User> findByEmployeeProfile(
            tn.knowflowai.backend.Entity.Enum.EmployeProfile employeeProfile
    );

    List<User> findByGender(
            tn.knowflowai.backend.Entity.Enum.Gender gender
    );

    List<User> findByAge(Integer age);

    List<User> findByAgeGreaterThanEqual(Integer age);

    List<User> findByAgeLessThanEqual(Integer age);

    boolean existsByEmail(String email);

    boolean existsByCin(String cin);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.familyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.cin) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<User> search(@Param("keyword") String keyword);

    @Query("""
        SELECT u FROM User u
        WHERE u.department.name = :departmentName
        AND u.role = :role
        """)
    List<User> findByDepartmentAndRole(
            @Param("departmentName") String departmentName,
            @Param("role") Role role
    );

boolean existsByRole(Role role);

}