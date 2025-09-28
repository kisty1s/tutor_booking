package com.giasu.tutor_booking.service.impl;

import com.giasu.tutor_booking.domain.user.AccountStatus;
import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.domain.user.UserRole;
import com.giasu.tutor_booking.dto.auth.AuthResponse;
import com.giasu.tutor_booking.dto.auth.LoginRequest;
import com.giasu.tutor_booking.dto.auth.RegisterRequest;
import com.giasu.tutor_booking.dto.auth.UserSummary;
import com.giasu.tutor_booking.exception.BusinessValidationException;
import com.giasu.tutor_booking.exception.ResourceAlreadyExistsException;
import com.giasu.tutor_booking.repository.UserAccountRepository;
import com.giasu.tutor_booking.security.JwtService;
import com.giasu.tutor_booking.service.AuthService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userAccountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResourceAlreadyExistsException("Email already registered: " + normalizedEmail);
        }

        UserRole requestedRole = Optional.ofNullable(request.role()).orElse(UserRole.PARENT);
        AccountStatus initialStatus = determineInitialStatus(requestedRole);

        UserAccount userAccount = UserAccount.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName().trim())
                .phoneNumber(trimToNull(request.phoneNumber()))
                .role(requestedRole)
                .status(initialStatus)
                .build();

        UserAccount saved = userAccountRepository.save(userAccount);
        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BusinessValidationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), userAccount.getPasswordHash())) {
            throw new BusinessValidationException("Invalid email or password");
        }

        validateAccountStatus(userAccount);

        Instant now = Instant.now();
        userAccount.setLastLoginAt(now);

        return buildAuthResponse(userAccount);
    }

    private AuthResponse buildAuthResponse(UserAccount userAccount) {
        String token = jwtService.generateToken(userAccount);
        Instant expiresAt = Instant.now().plus(jwtService.getExpirationMinutes(), ChronoUnit.MINUTES);

        UserSummary userSummary = new UserSummary(
                userAccount.getId(),
                userAccount.getEmail(),
                userAccount.getFullName(),
                userAccount.getPhoneNumber(),
                userAccount.getRole(),
                userAccount.getStatus()
        );

        return new AuthResponse(TOKEN_TYPE, token, expiresAt, userSummary);
    }

    private void validateAccountStatus(UserAccount userAccount) {
        AccountStatus status = userAccount.getStatus();
        switch (status) {
            case ACTIVE -> {
                // allow
            }
            case PENDING_APPROVAL -> throw new BusinessValidationException("Account is pending approval");
            case SUSPENDED -> throw new BusinessValidationException("Account is suspended");
            case DEACTIVATED -> throw new BusinessValidationException("Account is deactivated");
            default -> throw new BusinessValidationException("Account status is invalid");
        }
    }

    private AccountStatus determineInitialStatus(UserRole role) {
        if (role == UserRole.TUTOR) {
            return AccountStatus.PENDING_APPROVAL;
        }
        return AccountStatus.ACTIVE;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}