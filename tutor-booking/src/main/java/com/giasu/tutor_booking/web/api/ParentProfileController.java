package com.giasu.tutor_booking.web.api;

import com.giasu.tutor_booking.dto.user.ParentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentProfileResponse;
import com.giasu.tutor_booking.dto.user.ParentProfileUpdateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkResponse;
import com.giasu.tutor_booking.service.ParentProfileService;
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
@RequestMapping("/api/v1/parents")
@Validated
@RequiredArgsConstructor
public class ParentProfileController {

    private final ParentProfileService parentProfileService;

    @PostMapping
    public ResponseEntity<ParentProfileResponse> createParent(@Valid @RequestBody ParentProfileCreateRequest request) {
        ParentProfileResponse response = parentProfileService.createParentProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ParentProfileResponse>> listParents() {
        return ResponseEntity.ok(parentProfileService.listParentProfiles());
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<ParentProfileResponse> getParent(@PathVariable UUID parentId) {
        return ResponseEntity.ok(parentProfileService.getParentProfile(parentId));
    }

    @PutMapping("/{parentId}")
    public ResponseEntity<ParentProfileResponse> updateParent(@PathVariable UUID parentId,
                                                              @Valid @RequestBody ParentProfileUpdateRequest request) {
        return ResponseEntity.ok(parentProfileService.updateParentProfile(parentId, request));
    }

    @DeleteMapping("/{parentId}")
    public ResponseEntity<Void> deleteParent(@PathVariable UUID parentId) {
        parentProfileService.deleteParentProfile(parentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{parentId}/students")
    public ResponseEntity<ParentStudentLinkResponse> addStudent(@PathVariable UUID parentId,
                                                                @Valid @RequestBody ParentStudentLinkCreateRequest request) {
        ParentStudentLinkResponse response = parentProfileService.addStudentLink(parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{parentId}/students/{studentId}")
    public ResponseEntity<Void> removeStudent(@PathVariable UUID parentId, @PathVariable UUID studentId) {
        parentProfileService.removeStudentLink(parentId, studentId);
        return ResponseEntity.noContent().build();
    }
}
