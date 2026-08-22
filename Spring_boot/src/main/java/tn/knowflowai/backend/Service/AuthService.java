package tn.knowflowai.backend.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.knowflowai.backend.DTO.Auth.AuthResponse;
import tn.knowflowai.backend.DTO.Auth.DepartmentOption;
import tn.knowflowai.backend.DTO.Auth.ForgotPasswordRequest;
import tn.knowflowai.backend.DTO.Auth.LoginRequest;
import tn.knowflowai.backend.DTO.Auth.RegisterRequest;
import tn.knowflowai.backend.DTO.Auth.RegistrationOptionsResponse;
import tn.knowflowai.backend.DTO.Auth.ResetPasswordRequest;
import tn.knowflowai.backend.DTO.Auth.UserResponse;
import tn.knowflowai.backend.Entity.Department;
import tn.knowflowai.backend.Entity.Enum.EmployeProfile;
import tn.knowflowai.backend.Entity.Enum.Gender;
import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Entity.PasswordResetToken;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.DepartmentRepository;
import tn.knowflowai.backend.Repository.PasswordResetTokenRepository;
import tn.knowflowai.backend.Repository.UserRepository;




@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final EmailService emailService;
    private final ImageStorageService imageStorageService;
   public AuthService(
        UserRepository userRepository,
        DepartmentRepository departmentRepository,
        PasswordResetTokenRepository passwordResetTokenRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        EmailService emailService,
        ImageStorageService imageStorageService          // ← add this
) {
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.imageStorageService = imageStorageService;    // ← add this
}

    // =====================================================
    // REGISTER
    // =====================================================

   public UserResponse register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Email already exists");
    }

    if (userRepository.existsByCin(request.getCin())) {
        throw new RuntimeException("CIN already exists");
    }

    Department department = null;

    if (request.getDepartmentName() != null) {
        department = departmentRepository
                .findAll()
                .stream()
                .filter(d -> d.getName().equals(request.getDepartmentName()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Department not found")
                );
    }

    // ---------- Save image to folder & get short path ----------
    String imagePath = null;
    try {
        imagePath = imageStorageService.saveBase64Image(request.getUrlImage());
    } catch (Exception e) {
        throw new RuntimeException("Failed to save profile image", e);
    }
    // -----------------------------------------------------------

    User user = new User();

    user.setName(request.getName());
    user.setFamilyName(request.getFamilyName());
    user.setEmail(request.getEmail().toLowerCase());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    user.setCin(request.getCin());
    user.setUrlImage(imagePath);               // ← store only the path
    user.setPhoneNumber(request.getPhoneNumber());
    user.setAddress(request.getAddress());
    user.setAge(request.getAge());
    user.setGender(request.getGender());
    user.setEmployeeProfile(request.getEmployeeProfile());

    // Never allow ADMIN via public registration
    user.setRole(Role.EMPLOYEE);

    user.setDepartment(department);
    user.setEnabled(true);

    User savedUser = userRepository.save(user);

    return UserResponse.fromUser(savedUser);
}
    // =====================================================
    // LOGIN
    // =====================================================

    public AuthResponse login(
            LoginRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                    )
                );

        User user =
                userRepository
                    .findByEmail(
                        request.getEmail().toLowerCase()
                    )
                    .orElseThrow(() ->
                        new RuntimeException(
                            "User not found"
                        )
                    );

        String token =
                jwtService.generateToken(user);

        return new AuthResponse(
            token,
            UserResponse.fromUser(user)
        );
    }

    // =====================================================
    // FORGOT PASSWORD
    // =====================================================

    public void forgotPassword(
            ForgotPasswordRequest request
    ) {

        User user =
                userRepository.findByEmail(
                    request.getEmail().toLowerCase()
                ).orElse(null);

        /*
         * Do not reveal whether an email exists.
         *
         * This prevents user enumeration.
         */
        if (user == null) {
            return;
        }

        /*
         * Delete old reset tokens.
         */
        passwordResetTokenRepository
                .deleteByUserId(user.getId());

        String token =
                UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken(
                    token,
                    LocalDateTime.now().plusMinutes(15),
                    user
                );

        passwordResetTokenRepository.save(
                resetToken
        );

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                token
        );
    }

    // =====================================================
    // RESET PASSWORD
    // =====================================================

    public void resetPassword(
            ResetPasswordRequest request
    ) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                    .findByToken(request.getToken())
                    .orElseThrow(() ->
                        new RuntimeException(
                            "Invalid reset token"
                        )
                    );

        if (resetToken.isUsed()) {
            throw new RuntimeException(
                "Reset token has already been used"
            );
        }

        if (resetToken.getExpirationDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                "Reset token has expired"
            );
        }

        User user =
                resetToken.getUser();

        user.setPassword(
            passwordEncoder.encode(
                request.getNewPassword()
            )
        );

        userRepository.save(user);

        resetToken.setUsed(true);

        passwordResetTokenRepository.save(
                resetToken
        );
    }

    //regitraion_options
public RegistrationOptionsResponse getRegistrationOptions() {

    List<String> genders =
            Arrays.stream(Gender.values())
                    .map(Enum::name)
                    .toList();

    List<String> employeeProfiles =
            Arrays.stream(EmployeProfile.values())
                    .map(Enum::name)
                    .toList();

    List<DepartmentOption> departments =
            departmentRepository.findAll()
                    .stream()
                    .map(department ->
                            new DepartmentOption(
                                    department.getId(),
                                    department.getName()
                            )
                    )
                    .toList();

    return new RegistrationOptionsResponse(
            genders,
            employeeProfiles,
            departments
    );
}



    
}