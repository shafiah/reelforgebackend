package com.reelforge.repository;

import com.reelforge.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    
    Optional<VideoEntity> findByFileName(String fileName);
    
    List<VideoEntity> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<VideoEntity> findByMimeType(String mimeType);
    
    List<VideoEntity> findByResolution(String resolution);
}
