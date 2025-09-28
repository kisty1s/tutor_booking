package com.giasu.tutor_booking.service.impl;

import com.giasu.tutor_booking.domain.subject.Subject;
import com.giasu.tutor_booking.dto.subject.SubjectCreateRequest;
import com.giasu.tutor_booking.dto.subject.SubjectResponse;
import com.giasu.tutor_booking.dto.subject.SubjectUpdateRequest;
import com.giasu.tutor_booking.exception.ResourceAlreadyExistsException;
import com.giasu.tutor_booking.exception.ResourceNotFoundException;
import com.giasu.tutor_booking.repository.SubjectRepository;
import com.giasu.tutor_booking.service.SubjectService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public SubjectResponse createSubject(SubjectCreateRequest request) {
        String normalizedCode = normalizeCode(request.code());
        subjectRepository.findByCodeIgnoreCase(normalizedCode)
                .ifPresent(subject -> {
                    throw new ResourceAlreadyExistsException("Subject code already exists: " + normalizedCode);
                });
        String normalizedName = request.name().trim();
        if (subjectRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ResourceAlreadyExistsException("Subject name already exists: " + normalizedName);
        }

        boolean active = request.active() == null || request.active();

        Subject subject = Subject.builder()
                .code(normalizedCode)
                .name(normalizedName)
                .description(request.description())
                .active(active)
                .build();

        Subject saved = subjectRepository.save(subject);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> listSubjects() {
        return subjectRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectResponse updateSubject(UUID subjectId, SubjectUpdateRequest request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + subjectId));

        if (request.code() != null) {
            String normalizedCode = normalizeCode(request.code());
            if (!Objects.equals(subject.getCode(), normalizedCode)
                    && subjectRepository.findByCodeIgnoreCase(normalizedCode).isPresent()) {
                throw new ResourceAlreadyExistsException("Subject code already exists: " + normalizedCode);
            }
            subject.setCode(normalizedCode);
        }

        if (request.name() != null) {
            String trimmedName = request.name().trim();
            if (!trimmedName.equalsIgnoreCase(subject.getName())
                    && subjectRepository.existsByNameIgnoreCase(trimmedName)) {
                throw new ResourceAlreadyExistsException("Subject name already exists: " + trimmedName);
            }
            subject.setName(trimmedName);
        }

        if (request.description() != null) {
            subject.setDescription(request.description());
        }

        if (request.active() != null) {
            subject.setActive(request.active());
        }

        Subject saved = subjectRepository.save(subject);
        return toResponse(saved);
    }

    @Override
    public void deleteSubject(UUID subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found: " + subjectId);
        }
        subjectRepository.deleteById(subjectId);
    }

    private SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                subject.getDescription(),
                subject.isActive()
        );
    }

    private String normalizeCode(String rawCode) {
        return rawCode == null ? null : rawCode.trim().toUpperCase();
    }
}
