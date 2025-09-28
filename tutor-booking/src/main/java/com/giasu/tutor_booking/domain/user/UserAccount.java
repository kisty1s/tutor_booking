package com.giasu.tutor_booking.domain.user;

import com.giasu.tutor_booking.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_accounts")
public class UserAccount extends BaseEntity {

    @Email
    @NotBlank
    @Size(max = 180)
    @Column(name = "email", nullable = false, unique = true, length = 180)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Size(max = 120)
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Size(max = 30)
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AccountStatus status;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @OneToOne(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private TutorProfile tutorProfile;

    @OneToOne(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private StudentProfile studentProfile;

    @OneToOne(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private ParentProfile parentProfile;
}
