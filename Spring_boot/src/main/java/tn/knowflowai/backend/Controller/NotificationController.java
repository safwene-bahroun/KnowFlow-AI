package tn.knowflowai.backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.knowflowai.backend.DTO.NotificationResponse;
import tn.knowflowai.backend.Entity.Notification;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.UserRepository;
import tn.knowflowai.backend.Service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            Authentication authentication) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(notificationService.getByUser(user.getId()).stream()
                .map(NotificationResponse::from)
                .toList());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(Authentication authentication) {
        return ResponseEntity.ok(notificationService.countUnread(currentUser(authentication).getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        User user = currentUser(authentication);
        Notification notification = notificationService.getById(id);
        verifyOwner(notification, user);
        return ResponseEntity.ok(NotificationResponse.from(notificationService.markAsRead(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        User user = currentUser(authentication);
        verifyOwner(notificationService.getById(id), user);
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void verifyOwner(Notification notification, User user) {
        if (notification.getRecipient() == null
                || !notification.getRecipient().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You cannot access this notification");
        }
    }
}
