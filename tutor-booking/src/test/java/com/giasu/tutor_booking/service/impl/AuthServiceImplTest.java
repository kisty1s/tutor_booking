package com.giasu.tutor_booking.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.giasu.tutor_booking.domain.user.AccountStatus;
import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.domain.user.UserRole;
import com.giasu.tutor_booking.dto.auth.AuthResponse;
import com.giasu.tutor_booking.dto.auth.LoginRequest;
import com.giasu.tutor_booking.dto.auth.RegisterRequest;
import com.giasu.tutor_booking.exception.BusinessValidationException;
import com.giasu.tutor_booking.exception.ResourceAlreadyExistsException;
import com.giasu.tutor_booking.repository.UserAccountRepository;
import com.giasu.tutor_booking.security.JwtService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest baseRegisterRequest;

    @BeforeEach
    void setUp() {
        baseRegisterRequest = new RegisterRequest(
                "parent@example.com",
                "Password123",
                "Parent User",
                null,
                null
        );
    }

    @Test
    void register_ShouldCreateActiveParentAccount() {
        when(userAccountRepository.existsByEmailIgnoreCase("parent@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");

        UserAccount savedAccount = UserAccount.builder()
                .email("parent@example.com")
                .passwordHash("encoded-password")
                .fullName("Parent User")
                .role(UserRole.PARENT)
                .status(AccountStatus.ACTIVE)
                .build();
        savedAccount.setId(UUID.randomUUID());

        ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
        when(userAccountRepository.save(userCaptor.capture())).thenReturn(savedAccount);
        when(jwtService.generateToken(savedAccount)).thenReturn("jwt-token");
        when(jwtService.getExpirationMinutes()).thenReturn(120L);

        AuthResponse response = authService.register(baseRegisterRequest);

        UserAccount persisted = userCaptor.getValue();
        assertThat(persisted.getEmail()).isEqualTo("parent@example.com");
        assertThat(persisted.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(persisted.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.user().role()).isEqualTo(UserRole.PARENT);
        assertThat(response.user().status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.expiresAt()).isAfter(Instant.now().minusSeconds(5));
    }

    @Test
    void register_ShouldSetTutorAsPendingApproval() {
        RegisterRequest tutorRequest = new RegisterRequest(
                "tutor@example.com",
                "Password123",
                "Tutor User",
                "0123456789",
                UserRole.TUTOR
        );

        when(userAccountRepository.existsByEmailIgnoreCase("tutor@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");

        UserAccount savedAccount = UserAccount.builder()
                .email("tutor@example.com")
                .passwordHash("encoded-password")
                .fullName("Tutor User")
                .phoneNumber("0123456789")
                .role(UserRole.TUTOR)
                .status(AccountStatus.PENDING_APPROVAL)
                .build();
        savedAccount.setId(UUID.randomUUID());

        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedAccount);
        when(jwtService.generateToken(savedAccount)).thenReturn("jwt-token");
        when(jwtService.getExpirationMinutes()).thenReturn(120L);

        AuthResponse response = authService.register(tutorRequest);

        assertThat(response.user().role()).isEqualTo(UserRole.TUTOR);
        assertThat(response.user().status()).isEqualTo(AccountStatus.PENDING_APPROVAL);
    }

    @Test
    void register_ShouldThrowWhenEmailAlreadyExists() {
        when(userAccountRepository.existsByEmailIgnoreCase("parent@example.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(baseRegisterRequest));
        verify(userAccountRepository, never()).save(any(UserAccount.class));
    }

    @Test
    void login_ShouldReturnTokenWhenCredentialsValid() {
        LoginRequest loginRequest = new LoginRequest("parent@example.com", "Password123");
        UserAccount existingAccount = UserAccount.builder()
                .email("parent@example.com")
                .passwordHash("encoded-password")
                .fullName("Parent User")
                .role(UserRole.PARENT)
                .status(AccountStatus.ACTIVE)
                .build();
        existingAccount.setId(UUID.randomUUID());

        when(userAccountRepository.findByEmailIgnoreCase("parent@example.com")).thenReturn(Optional.of(existingAccount));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(existingAccount)).thenReturn("jwt-token");
        when(jwtService.getExpirationMinutes()).thenReturn(120L);

        AuthResponse response = authService.login(loginRequest);

        assertThat(existingAccount.getLastLoginAt()).isNotNull();
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.user().email()).isEqualTo("parent@example.com");
    }

    @Test
    void login_ShouldThrowWhenPasswordDoesNotMatch() {
        LoginRequest loginRequest = new LoginRequest("parent@example.com", "WrongPassword");
        UserAccount existingAccount = UserAccount.builder()
                .email("parent@example.com")
                .passwordHash("encoded-password")
                .fullName("Parent User")
                .role(UserRole.PARENT)
                .status(AccountStatus.ACTIVE)
                .build();

        when(userAccountRepository.findByEmailIgnoreCase("parent@example.com")).thenReturn(Optional.of(existingAccount));
        when(passwordEncoder.matches("WrongPassword", "encoded-password")).thenReturn(false);

        assertThrows(BusinessValidationException.class, () -> authService.login(loginRequest));
        verify(jwtService, never()).generateToken(any(UserAccount.class));
    }

    @ParameterizedTest
    @EnumSource(value = AccountStatus.class, names = {"PENDING_APPROVAL", "SUSPENDED", "DEACTIVATED"})
    void login_ShouldRejectInactiveStatuses(AccountStatus status) {
        LoginRequest loginRequest = new LoginRequest("parent@example.com", "Password123");
        UserAccount existingAccount = UserAccount.builder()
                .email("parent@example.com")
                .passwordHash("encoded-password")
                .fullName("Parent User")
                .role(UserRole.PARENT)
                .status(status)
                .build();

        when(userAccountRepository.findByEmailIgnoreCase("parent@example.com")).thenReturn(Optional.of(existingAccount));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);

        assertThrows(BusinessValidationException.class, () -> authService.login(loginRequest));
        verify(jwtService, never()).generateToken(any(UserAccount.class));
    }

    @Test
    void login_ShouldThrowWhenAccountMissing() {
        LoginRequest loginRequest = new LoginRequest("missing@example.com", "Password123");
        when(userAccountRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessValidationException.class, () -> authService.login(loginRequest));
        verify(passwordEncoder, never()).matches(eq("Password123"), anyString());
    }
}