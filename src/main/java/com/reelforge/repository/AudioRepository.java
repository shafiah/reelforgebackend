package com.reelforge.repository;

import com.reelforge.entity.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AudioRepository extends JpaRepository<AudioEntity, Long> {
    
    Optional<AudioEntity> findByFileName(String fileName);
    
    List<AudioEntity> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<AudioEntity> findByMimeType(String mimeType);
    
    List<AudioEntity> findByBitrate(String bitrate);
}
