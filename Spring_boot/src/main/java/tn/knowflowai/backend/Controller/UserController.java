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

import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // CREATE USER
    // POST /api/admin/users
    // =========================================================
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {

        User createdUser = userService.create(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    // =========================================================
    // GET ALL USERS
    // GET /api/admin/users
    // =========================================================
    @GetMapping
    public ResponseEntity<List<User>> getAll() {

        return ResponseEntity.ok(
                userService.getAll()
        );
    }

    // =========================================================
    // GET USER BY ID
    // GET /api/admin/users/{id}
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.getById(id)
        );
    }

    // =========================================================
    // GET USER BY EMAIL
    // GET /api/admin/users/email/{email}
    // =========================================================
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getByEmail(
            @PathVariable String email
    ) {

        return ResponseEntity.ok(
                userService.getByEmail(email)
        );
    }

    // =========================================================
    // GET USER BY CIN
    // GET /api/admin/users/cin/{cin}
    // =========================================================
    @GetMapping("/cin/{cin}")
    public ResponseEntity<User> getByCin(
            @PathVariable String cin
    ) {

        return ResponseEntity.ok(
                userService.getByCin(cin)
        );
    }

    // =========================================================
    // SEARCH USERS
    // GET /api/admin/users/search?keyword=...
    // =========================================================
    @GetMapping("/search")
    public ResponseEntity<List<User>> search(
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                userService.search(keyword)
        );
    }

    // =========================================================
    // GET USERS BY NAME
    // GET /api/admin/users/name/{name}
    // =========================================================
    @GetMapping("/name/{name}")
    public ResponseEntity<List<User>> getByName(
            @PathVariable String name
    ) {

        return ResponseEntity.ok(
                userService.getByName(name)
        );
    }

    // =========================================================
    // GET USERS BY FAMILY NAME
    // GET /api/admin/users/family-name/{familyName}
    // =========================================================
    @GetMapping("/family-name/{familyName}")
    public ResponseEntity<List<User>> getByFamilyName(
            @PathVariable String familyName
    ) {

        return ResponseEntity.ok(
                userService.getByFamilyName(familyName)
        );
    }

    // =========================================================
    // GET USERS BY ROLE
    // GET /api/admin/users/role/{role}
    // =========================================================
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getByRole(
            @PathVariable Role role
    ) {

        return ResponseEntity.ok(
                userService.getByRole(role)
        );
    }

    // =========================================================
    // GET USERS BY DEPARTMENT
    // GET /api/admin/users/department/{departmentName}
    // =========================================================
    @GetMapping("/department/{departmentName}")
    public ResponseEntity<List<User>> getByDepartment(
            @PathVariable String departmentName
    ) {

        return ResponseEntity.ok(
                userService.getByDepartment(departmentName)
        );
    }

    // =========================================================
    // UPDATE USER
    // PUT /api/admin/users/{id}
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @RequestBody User updatedUser
    ) {

        return ResponseEntity.ok(
                userService.update(id, updatedUser)
        );
    }

    // =========================================================
    // UPDATE PASSWORD
    // PUT /api/admin/users/{id}/password
    // =========================================================
    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(
            @PathVariable Long id,
            @RequestBody String newPassword
    ) {

        return ResponseEntity.ok(
                userService.updatePassword(
                        id,
                        newPassword
                )
        );
    }

    // =========================================================
    // DELETE USER
    // DELETE /api/admin/users/{id}
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // CHECK IF USER EXISTS
    // GET /api/admin/users/{id}/exists
    // =========================================================
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.exists(id)
        );
    }
}