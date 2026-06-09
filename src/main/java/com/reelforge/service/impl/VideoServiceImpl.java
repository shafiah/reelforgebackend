package com.reelforge.service.impl;

import com.reelforge.dto.VideoProcessingResponse;
import com.reelforge.dto.VideoUploadResponse;
import com.reelforge.entity.VideoEntity;
import com.reelforge.exception.ResourceNotFoundException;
import com.reelforge.repository.VideoRepository;
import com.reelforge.service.FFmpegService;
import com.reelforge.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VideoServiceImpl implements VideoService {
    
    private final VideoRepository videoRepository;
    private final FFmpegService ffmpegService;
    private static final String AUDIO_PATH = "uploads/audios/voiceover.mp3";
    private static final String PROCESSED_DIR = "uploads/processed";
    
    @Override
    public VideoUploadResponse uploadVideo(VideoEntity video) {
        log.debug("Uploading video: {} with file path: {}", video.getFileName(), video.getFilePath());
        
        try {
            VideoEntity savedVideo = videoRepository.save(video);
            log.info("Video saved successfully with ID: {} and filename: {}", savedVideo.getId(), savedVideo.getFileName());
            
            VideoUploadResponse response = VideoUploadResponse.builder()
                .videoId(savedVideo.getId())
                .fileName(savedVideo.getFileName())
                .fileSizeBytes(savedVideo.getFileSizeBytes())
                .mimeType(savedVideo.getMimeType())
                .durationSeconds(savedVideo.getDurationSeconds())
                .resolution(savedVideo.getResolution())
                .uploadedAt(savedVideo.getUploadedAt())
                .success(true)
                .message("Video uploaded successfully")
                .build();
            
            return response;
        } catch (Exception e) {
            log.error("Error uploading video: {}", video.getFileName(), e);
            throw e;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<VideoEntity> getVideoById(Long id) {
        log.debug("Fetching video by ID: {}", id);
        return videoRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VideoEntity> getAllVideos() {
        log.debug("Fetching all videos");
        return videoRepository.findAll();
    }
    
    @Override
    public VideoEntity updateVideo(Long id, VideoEntity video) {
        log.debug("Updating video with ID: {}", id);
        
        VideoEntity existingVideo = videoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Video not found with id: {}", id);
                return new ResourceNotFoundException("Video not found with id: " + id);
            });
        
        if (video.getFileName() != null) {
            existingVideo.setFileName(video.getFileName());
        }
        if (video.getResolution() != null) {
            existingVideo.setResolution(video.getResolution());
        }
        if (video.getDurationSeconds() != null) {
            existingVideo.setDurationSeconds(video.getDurationSeconds());
        }
        
        VideoEntity updatedVideo = videoRepository.save(existingVideo);
        log.info("Video updated successfully with ID: {}", id);
        return updatedVideo;
    }
    
    @Override
    public void deleteVideo(Long id) {
        log.debug("Deleting video with ID: {}", id);
        
        if (!videoRepository.existsById(id)) {
            log.warn("Video not found with id: {}", id);
            throw new ResourceNotFoundException("Video not found with id: " + id);
        }
        videoRepository.deleteById(id);
        log.info("Video deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VideoEntity> getVideosByResolution(String resolution) {
        log.debug("Fetching videos by resolution: {}", resolution);
        return videoRepository.findByResolution(resolution);
    }
    
    @Override
    public VideoProcessingResponse processVideo(Long videoId) {
        log.info("Starting video processing for videoId: {}", videoId);
        
        // Find VideoEntity by videoId
        VideoEntity video = videoRepository.findById(videoId)
            .orElseThrow(() -> {
                log.warn("Video not found with id: {}", videoId);
                return new ResourceNotFoundException("Video not found with id: " + videoId);
            });
        
        log.debug("Found video: {} with path: {}", video.getFileName(), video.getFilePath());
        
        try {
            // Create processed directory if it doesn't exist
            File processedDirectory = new File(PROCESSED_DIR);
            if (!processedDirectory.exists()) {
                if (!processedDirectory.mkdirs()) {
                    log.error("Failed to create processed directory: {}", PROCESSED_DIR);
                    throw new RuntimeException("Failed to create processed directory: " + PROCESSED_DIR);
                }
                log.info("Created processed directory: {}", PROCESSED_DIR);
            }
            
            // Generate output file path with UUID
            String outputFileName = UUID.randomUUID().toString() + ".mp4";
            String outputVideoPath = PROCESSED_DIR + File.separator + outputFileName;
            
            log.debug("Generated output path: {}", outputVideoPath);
            
            // Call FFmpegService to replace audio
            log.debug("Calling FFmpegService to replace audio in video");
            String processedPath = ffmpegService.replaceAudio(
                video.getFilePath(),
                AUDIO_PATH,
                outputVideoPath
            );
            
            log.debug("FFmpeg processing completed successfully. Output: {}", processedPath);
            
            // Update VideoEntity with processed video path and timestamp
            video.setFilePath(processedPath);
            video.setProcessedAt(LocalDateTime.now());
            
            VideoEntity updatedVideo = videoRepository.save(video);
            log.info("Video entity updated successfully with processed video path and timestamp for videoId: {}", videoId);
            
            // Return response
            VideoProcessingResponse response = VideoProcessingResponse.builder()
                .videoId(updatedVideo.getId())
                .processedVideoPath(processedPath)
                .success(true)
                .message("Video processed successfully")
                .build();
            
            log.info("Video processing completed successfully for videoId: {}", videoId);
            return response;
            
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during video processing: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("FFmpeg processing failed for videoId: {}", videoId, e);
            throw new RuntimeException("Video processing failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during video processing for videoId: {}", videoId, e);
            throw new RuntimeException("Unexpected error during video processing: " + e.getMessage(), e);
        }
    }
}
