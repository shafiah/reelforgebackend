package com.reelforge.dto;

import com.reelforge.enums.JobStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingStatusResponse {
    
    private Long jobId;
    
    private String jobName;
    
    private JobStatus status;
    
    private Long videoId;
    
    private Long audioId;
    
    private String outputPath;
    
    private Double progressPercentage;
    
    private String errorMessage;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
}
