package com.example.education_service.controller;

import com.example.education_service.model.ChronicCareEnrollment;
import com.example.education_service.model.WellnessContent;
import com.example.education_service.repository.ChronicCareEnrollmentRepository;
import com.example.education_service.repository.WellnessContentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wellness")
@Tag(name = "Wellness", description = "Fitness tips, suggestions, and chronic disease home assistance")
public class WellnessController {

    @Autowired private WellnessContentRepository contentRepo;
    @Autowired private ChronicCareEnrollmentRepository enrollmentRepo;

    @Operation(summary = "Get all fitness tips")
    @GetMapping("/fitness-tips")
    public ResponseEntity<?> getFitnessTips() {
        return ResponseEntity.ok(contentRepo.findByCategoryAndActiveTrue(WellnessContent.ContentCategory.FITNESS_TIP));
    }

    @Operation(summary = "Get chronic disease home assistance programs")
    @GetMapping("/chronic-care")
    public ResponseEntity<?> getChronicCarePrograms() {
        return ResponseEntity.ok(contentRepo.findByCategoryAndActiveTrue(WellnessContent.ContentCategory.CHRONIC_CARE_PROGRAM));
    }

    @Operation(summary = "Enroll in a chronic care home assistance program")
    @PostMapping("/chronic-care/enroll")
    public ResponseEntity<?> enrollInProgram(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        UUID programId = UUID.fromString(body.get("programId"));

        if (enrollmentRepo.existsByUserIdAndProgramId(userId, programId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Already enrolled in this program."));
        }

        ChronicCareEnrollment enrollment = new ChronicCareEnrollment();
        enrollment.setUserId(userId);
        enrollment.setProgramId(programId);
        enrollment.setNextHomeVisit(LocalDateTime.now().plusWeeks(2));
        return ResponseEntity.ok(enrollmentRepo.save(enrollment));
    }

    @Operation(summary = "Get user's chronic care enrollments")
    @GetMapping("/chronic-care/enrollments/{userId}")
    public ResponseEntity<?> getEnrollments(@PathVariable UUID userId) {
        return ResponseEntity.ok(enrollmentRepo.findByUserId(userId));
    }
}
