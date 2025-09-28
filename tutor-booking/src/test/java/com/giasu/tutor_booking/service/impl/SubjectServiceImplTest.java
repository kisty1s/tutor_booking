package com.giasu.tutor_booking.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.giasu.tutor_booking.domain.subject.Subject;
import com.giasu.tutor_booking.dto.subject.SubjectCreateRequest;
import com.giasu.tutor_booking.exception.ResourceAlreadyExistsException;
import com.giasu.tutor_booking.repository.SubjectRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private SubjectCreateRequest request;

    @BeforeEach
    void setUp() {
        request = new SubjectCreateRequest("math", "Mathematics", "Algebra and calculus", null);
    }

    @Test
    void createSubject_ShouldPersistNewSubject() {
        Subject savedSubject = Subject.builder()
                .code("MATH")
                .name("Mathematics")
                .description("Algebra and calculus")
                .active(true)
                .build();
        savedSubject.setId(UUID.randomUUID());

        when(subjectRepository.findByCodeIgnoreCase("MATH")).thenReturn(Optional.empty());
        when(subjectRepository.existsByNameIgnoreCase("Mathematics")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(savedSubject);

        var response = subjectService.createSubject(request);

        assertThat(response.code()).isEqualTo("MATH");
        assertThat(response.name()).isEqualTo("Mathematics");
        assertThat(response.active()).isTrue();
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void createSubject_ShouldThrowWhenCodeExists() {
        when(subjectRepository.findByCodeIgnoreCase("MATH")).thenReturn(Optional.of(new Subject()));

        assertThrows(ResourceAlreadyExistsException.class, () -> subjectService.createSubject(request));
    }
}
