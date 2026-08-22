package tn.knowflowai.backend.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import tn.knowflowai.backend.Entity.User;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }

 public String generateToken(User user) {

    if (user == null) {
        throw new IllegalArgumentException("User cannot be null");
    }

    String roleName = user.getRole() == null ? null : user.getRole().name();
    if (roleName != null && !roleName.startsWith("ROLE_")) {
        roleName = "ROLE_" + roleName;
    }

    String employeeProfile = user.getEmployeeProfile() == null
            ? null
            : user.getEmployeeProfile().name();

    var builder = Jwts.builder()
            .subject(user.getEmail())
            .claim("userId", user.getId())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(signingKey);

    if (roleName != null) {
        builder.claim("role", roleName);
    }

    if (employeeProfile != null) {
        builder.claim("employeeProfile", employeeProfile);
    }

    return builder.compact();
}

    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(
                    (javax.crypto.SecretKey) signingKey
                )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}