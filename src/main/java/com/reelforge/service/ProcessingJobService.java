package com.reelforge.service;

import com.reelforge.dto.ProcessingStatusResponse;
import com.reelforge.entity.ProcessingJobEntity;
import com.reelforge.enums.JobStatus;

import java.util.List;
import java.util.Optional;

public interface ProcessingJobService {
    
    ProcessingJobEntity createJob(ProcessingJobEntity job);
    
    Optional<ProcessingJobEntity> getJobById(Long id);
    
    List<ProcessingJobEntity> getAllJobs();
    
    ProcessingStatusResponse getJobStatus(Long jobId);
    
    ProcessingJobEntity updateJobStatus(Long jobId, JobStatus status);
    
    List<ProcessingJobEntity> getJobsByStatus(JobStatus status);
    
    List<ProcessingJobEntity> getJobsByVideoId(Long videoId);
    
    void deleteJob(Long jobId);
}
