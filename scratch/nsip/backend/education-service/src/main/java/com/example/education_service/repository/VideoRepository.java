package com.example.education_service.repository;

import com.example.education_service.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    List<Video> findByCourseId(UUID courseId);
}
