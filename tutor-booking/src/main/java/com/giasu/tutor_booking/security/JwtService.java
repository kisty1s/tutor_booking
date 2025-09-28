package com.giasu.tutor_booking.security;

import com.giasu.tutor_booking.config.JwtProperties;
import com.giasu.tutor_booking.domain.user.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMinutes;

    public JwtService(JwtProperties properties) {
        String secret = Objects.requireNonNullElse(properties.secret(), "ChangeMeChangeMeChangeMeChangeMe");
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = properties.expirationMinutes();
    }

    public String generateToken(UserAccount userAccount, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userAccount.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .addClaims(extraClaims)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserAccount userAccount) {
        return generateToken(userAccount, Map.of());
    }

    public boolean isTokenValid(String token, UserAccountPrincipal principal) {
        String username = extractUsername(token);
        return username.equalsIgnoreCase(principal.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }
}