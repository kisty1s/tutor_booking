package com.giasu.tutor_booking.domain.user;

import com.giasu.tutor_booking.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "student_profiles")
public class StudentProfile extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @Column(name = "grade_level", length = 50)
    private String gradeLevel;

    @Column(name = "learning_goals", length = 500)
    private String learningGoals;

    @Column(name = "preferred_learning_mode", length = 50)
    private String preferredLearningMode;

    @Column(name = "time_zone", length = 60)
    private String timeZone;

    @Builder.Default
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParentStudentLink> parentLinks = new HashSet<>();
}
