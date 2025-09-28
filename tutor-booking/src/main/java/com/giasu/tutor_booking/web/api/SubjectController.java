package com.giasu.tutor_booking.web.api;

import com.giasu.tutor_booking.dto.subject.SubjectCreateRequest;
import com.giasu.tutor_booking.dto.subject.SubjectResponse;
import com.giasu.tutor_booking.dto.subject.SubjectUpdateRequest;
import com.giasu.tutor_booking.service.SubjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subjects")
@Validated
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectCreateRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> listSubjects() {
        return ResponseEntity.ok(subjectService.listSubjects());
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> updateSubject(@PathVariable UUID subjectId,
                                                         @Valid @RequestBody SubjectUpdateRequest request) {
        return ResponseEntity.ok(subjectService.updateSubject(subjectId, request));
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID subjectId) {
        subjectService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build();
    }
}
