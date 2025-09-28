package com.giasu.tutor_booking.web.api;

import com.giasu.tutor_booking.dto.user.StudentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.StudentProfileResponse;
import com.giasu.tutor_booking.dto.user.StudentProfileUpdateRequest;
import com.giasu.tutor_booking.service.StudentProfileService;
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
@RequestMapping("/api/v1/students")
@Validated
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping
    public ResponseEntity<StudentProfileResponse> createStudent(@Valid @RequestBody StudentProfileCreateRequest request) {
        StudentProfileResponse response = studentProfileService.createStudentProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StudentProfileResponse>> listStudents() {
        return ResponseEntity.ok(studentProfileService.listStudentProfiles());
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentProfileResponse> getStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studentProfileService.getStudentProfile(studentId));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentProfileResponse> updateStudent(@PathVariable UUID studentId,
                                                                 @Valid @RequestBody StudentProfileUpdateRequest request) {
        return ResponseEntity.ok(studentProfileService.updateStudentProfile(studentId, request));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID studentId) {
        studentProfileService.deleteStudentProfile(studentId);
        return ResponseEntity.noContent().build();
    }
}
