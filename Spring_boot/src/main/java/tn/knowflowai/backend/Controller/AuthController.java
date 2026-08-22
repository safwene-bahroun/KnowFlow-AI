package tn.knowflowai.backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.knowflowai.backend.DTO.Auth.AuthResponse;
import tn.knowflowai.backend.DTO.Auth.ForgotPasswordRequest;
import tn.knowflowai.backend.DTO.Auth.LoginRequest;
import tn.knowflowai.backend.DTO.Auth.RegisterRequest;
import tn.knowflowai.backend.DTO.Auth.RegistrationOptionsResponse;
import tn.knowflowai.backend.DTO.Auth.ResetPasswordRequest;
import tn.knowflowai.backend.DTO.Auth.UserResponse;
import tn.knowflowai.backend.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    // =====================================================
    // REGISTER
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        UserResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // FORGOT PASSWORD
    // =====================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody
            ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
            "If the email exists, a password reset link "
            + "has been sent."
        );
    }

    // =====================================================
    // RESET PASSWORD
    // =====================================================

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody
            ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
            "Password has been reset successfully."
        );
    }


@GetMapping("/registration-options")
public ResponseEntity<RegistrationOptionsResponse> getRegistrationOptions() {

    return ResponseEntity.ok(
        authService.getRegistrationOptions()
    );
}
















}