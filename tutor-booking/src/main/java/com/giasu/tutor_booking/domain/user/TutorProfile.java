package com.giasu.tutor_booking.domain.user;

import com.giasu.tutor_booking.common.BaseEntity;
import com.giasu.tutor_booking.domain.subject.TutorSubject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "tutor_profiles")
public class TutorProfile extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @Column(name = "headline", length = 150)
    private String headline;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "qualification_summary", length = 500)
    private String qualificationSummary;

    @Column(name = "city", length = 120)
    private String city;

    @Column(name = "country", length = 120)
    private String country;

    @Column(name = "teaching_modes", length = 120)
    private String teachingModes;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Builder.Default
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TutorSubject> tutorSubjects = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TutorAvailability> availabilities = new HashSet<>();
}

