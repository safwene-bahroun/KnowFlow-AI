package tn.knowflowai.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.knowflowai.backend.Entity.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUserId(Long userId);
}