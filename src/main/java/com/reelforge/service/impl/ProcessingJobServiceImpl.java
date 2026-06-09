package com.reelforge.service.impl;

import com.reelforge.dto.ProcessingStatusResponse;
import com.reelforge.entity.ProcessingJobEntity;
import com.reelforge.enums.JobStatus;
import com.reelforge.exception.ResourceNotFoundException;
import com.reelforge.repository.ProcessingJobRepository;
import com.reelforge.service.ProcessingJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessingJobServiceImpl implements ProcessingJobService {
    
    private final ProcessingJobRepository processingJobRepository;
    
    @Override
    public ProcessingJobEntity createJob(ProcessingJobEntity job) {
        return processingJobRepository.save(job);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessingJobEntity> getJobById(Long id) {
        return processingJobRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProcessingJobEntity> getAllJobs() {
        return processingJobRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProcessingStatusResponse getJobStatus(Long jobId) {
        ProcessingJobEntity job = processingJobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        return ProcessingStatusResponse.builder()
            .jobId(job.getId())
            .jobName(job.getJobName())
            .status(job.getStatus())
            .videoId(job.getVideoId())
            .audioId(job.getAudioId())
            .outputPath(job.getOutputPath())
            .progressPercentage(job.getProgressPercentage())
            .errorMessage(job.getErrorMessage())
            .createdAt(job.getCreatedAt())
            .startedAt(job.getStartedAt())
            .completedAt(job.getCompletedAt())
            .build();
    }
    
    @Override
    public ProcessingJobEntity updateJobStatus(Long jobId, JobStatus status) {
        ProcessingJobEntity job = processingJobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        job.setStatus(status);
        
        if (status == JobStatus.PROCESSING && job.getStartedAt() == null) {
            job.setStartedAt(LocalDateTime.now());
        }
        
        if ((status == JobStatus.COMPLETED || status == JobStatus.FAILED) && job.getCompletedAt() == null) {
            job.setCompletedAt(LocalDateTime.now());
        }
        
        return processingJobRepository.save(job);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProcessingJobEntity> getJobsByStatus(JobStatus status) {
        return processingJobRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProcessingJobEntity> getJobsByVideoId(Long videoId) {
        return processingJobRepository.findByVideoId(videoId);
    }
    
    @Override
    public void deleteJob(Long jobId) {
        if (!processingJobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }
        processingJobRepository.deleteById(jobId);
    }
}
