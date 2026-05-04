package com.example.education_service.controller;

import com.example.education_service.model.Course;
import com.example.education_service.model.Video;
import com.example.education_service.repository.CourseRepository;
import com.example.education_service.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable String category) {
        Course.CourseCategory courseCategory = Course.CourseCategory.valueOf(category.toUpperCase());
        return ResponseEntity.ok(courseRepository.findByCategoryAndActiveTrue(courseCategory));
    }

    @GetMapping("/{courseId}/videos")
    public ResponseEntity<List<Video>> getVideosByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(videoRepository.findByCourseId(courseId));
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @PostMapping("/{courseId}/videos")
    public ResponseEntity<Video> addVideo(@PathVariable UUID courseId, @RequestBody Video video) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));
        video.setCourse(course);
        return ResponseEntity.ok(videoRepository.save(video));
    }
}
