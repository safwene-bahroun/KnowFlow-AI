package tn.knowflowai.backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.DTO.Auth.UserResponse;
import tn.knowflowai.backend.DTO.ProfileRequest;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Service.UserService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(toResponse(
                userService.getByEmail(authentication.getName())));
    }

    @PutMapping
        public ResponseEntity<UserResponse> updateProfile(
            @RequestBody ProfileRequest request,
            Authentication authentication) throws java.io.IOException {
        User user = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(toResponse(user));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.fromUser(user);
    }
}