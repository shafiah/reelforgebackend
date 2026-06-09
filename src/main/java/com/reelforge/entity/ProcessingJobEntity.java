package com.reelforge.entity;

import com.reelforge.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processing_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingJobEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String jobName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;
    
    @Column(nullable = false)
    private Long videoId;
    
    private Long audioId;
    
    private String outputPath;
    
    private String errorMessage;
    
    private Double progressPercentage;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = JobStatus.PENDING;
        }
    }
}
