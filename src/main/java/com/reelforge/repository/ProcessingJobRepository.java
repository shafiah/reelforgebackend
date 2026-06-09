package com.reelforge.repository;

import com.reelforge.entity.ProcessingJobEntity;
import com.reelforge.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessingJobRepository extends JpaRepository<ProcessingJobEntity, Long> {
    
    List<ProcessingJobEntity> findByStatus(JobStatus status);
    
    List<ProcessingJobEntity> findByVideoId(Long videoId);
    
    List<ProcessingJobEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<ProcessingJobEntity> findByStatusAndCreatedAtBetween(JobStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
