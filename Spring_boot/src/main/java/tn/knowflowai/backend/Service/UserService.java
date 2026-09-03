package tn.knowflowai.backend.Service;

import java.io.IOException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.knowflowai.backend.DTO.ProfileRequest;
import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.DepartmentRepository;
import tn.knowflowai.backend.Repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageStorageService imageStorageService;

    public UserService(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder,
            ImageStorageService imageStorageService
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageStorageService = imageStorageService;
    }

    // CREATE
    public User create(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (user.getCin() != null && !user.getCin().isBlank() && userRepository.existsByCin(user.getCin())) {
            throw new RuntimeException("CIN already exists");
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            validatePassword(user.getPassword());
            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        // Handle profile image
        if (user.getUrlImage() != null && user.getUrlImage().startsWith("data:image")) {
            try {
                user.setUrlImage(imageStorageService.saveBase64Image(user.getUrlImage()));
            } catch (IOException e) {
                System.err.println("Failed to save profile image: " + e.getMessage());
            }
        }

        // Handle department
        if (user.getDepartment() != null && user.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(user.getDepartment().getId()).orElse(null);
            user.setDepartment(dept);
        } else {
            user.setDepartment(null);
        }

        return userRepository.save(user);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    // GET BY EMAIL
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    // GET BY CIN
    @Transactional(readOnly = true)
    public User getByCin(String cin) {
        return userRepository.findByCin(cin)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    // SEARCH
    @Transactional(readOnly = true)
    public List<User> search(String keyword) {
        return userRepository.search(keyword);
    }

    // GET BY NAME
    @Transactional(readOnly = true)
    public List<User> getByName(String name) {
        return userRepository
                .findByNameContainingIgnoreCase(name);
    }

    // GET BY FAMILY NAME
    @Transactional(readOnly = true)
    public List<User> getByFamilyName(String familyName) {
        return userRepository
                .findByFamilyNameContainingIgnoreCase(familyName);
    }

    // GET BY ROLE
    @Transactional(readOnly = true)
    public List<User> getByRole(
            tn.knowflowai.backend.Entity.Enum.Role role
    ) {
        return userRepository.findByRole(role);
    }

    // GET BY DEPARTMENT
    @Transactional(readOnly = true)
    public List<User> getByDepartment(String departmentName) {
        return userRepository.findByDepartmentName(departmentName);
    }

    // UPDATE
    public User update(Long id, User updatedUser) {

        User user = getById(id);

        if (!user.getEmail().equalsIgnoreCase(updatedUser.getEmail()) && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (updatedUser.getCin() != null && !updatedUser.getCin().isBlank() &&
                (user.getCin() == null || !user.getCin().equalsIgnoreCase(updatedUser.getCin())) &&
                userRepository.existsByCin(updatedUser.getCin())) {
            throw new RuntimeException("CIN already exists");
        }

        user.setName(updatedUser.getName());
        user.setFamilyName(updatedUser.getFamilyName());
        user.setEmail(updatedUser.getEmail());
        user.setCin(updatedUser.getCin());
        user.setAge(updatedUser.getAge());
        user.setGender(updatedUser.getGender());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAddress(updatedUser.getAddress());
        user.setRole(updatedUser.getRole());
        user.setEmployeeProfile(updatedUser.getEmployeeProfile());
        user.setEnabled(updatedUser.isEnabled());

        // Update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            validatePassword(updatedUser.getPassword());
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Handle profile image update
        if (updatedUser.getUrlImage() != null && updatedUser.getUrlImage().startsWith("data:image")) {
            try {
                user.setUrlImage(imageStorageService.saveBase64Image(updatedUser.getUrlImage()));
            } catch (IOException e) {
                System.err.println("Failed to save profile image on update: " + e.getMessage());
            }
        } else if (updatedUser.getUrlImage() != null) {
            user.setUrlImage(updatedUser.getUrlImage());
        }

        // Handle department update
        if (updatedUser.getDepartment() != null && updatedUser.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(updatedUser.getDepartment().getId()).orElse(null);
            user.setDepartment(dept);
        } else {
            user.setDepartment(null);
        }

        return userRepository.save(user);
    }

    // UPDATE PASSWORD
    public User updatePassword(
            Long id,
            String newPassword
    ) {

        User user = getById(id);

        validatePassword(newPassword);
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        return userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password.length() < 6 ||
                !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$")) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters, including uppercase, lowercase, number and special character"
            );
        }
    }

    public User updateProfile(String currentEmail, ProfileRequest request) throws IOException {
        User user = findByEmail(currentEmail);

        user.setName(request.getName());
        user.setFamilyName(request.getFamilyName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        if (request.getUrlImage() != null && !request.getUrlImage().isBlank()) {
            user.setUrlImage(imageStorageService.saveBase64Image(request.getUrlImage()));
        }

        return userRepository.save(user);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // DELETE
    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    // EXISTS
    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }
}