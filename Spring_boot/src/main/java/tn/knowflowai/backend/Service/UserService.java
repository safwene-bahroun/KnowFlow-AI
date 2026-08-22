package tn.knowflowai.backend.Service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    public User create(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByCin(user.getCin())) {
            throw new RuntimeException("CIN already exists");
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

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

        user.setName(updatedUser.getName());
        user.setFamilyName(updatedUser.getFamilyName());
        user.setEmail(updatedUser.getEmail());
        user.setCin(updatedUser.getCin());
        user.setAge(updatedUser.getAge());
        user.setGender(updatedUser.getGender());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAddress(updatedUser.getAddress());
        user.setUrlImage(updatedUser.getUrlImage());
        user.setRole(updatedUser.getRole());
        user.setEmployeeProfile(updatedUser.getEmployeeProfile());
        user.setDepartment(updatedUser.getDepartment());

        return userRepository.save(user);
    }

    // UPDATE PASSWORD
    public User updatePassword(
            Long id,
            String newPassword
    ) {

        User user = getById(id);

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        return userRepository.save(user);
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