package com.example.education_service.repository;

import com.example.education_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByActiveTrue();
    List<Course> findByCategoryAndActiveTrue(Course.CourseCategory category);
}
