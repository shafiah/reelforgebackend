package com.reelforge.controller;

import com.reelforge.dto.VideoProcessingResponse;
import com.reelforge.dto.VideoProcessRequest;

import com.reelforge.dto.VideoUploadResponse;
import com.reelforge.entity.VideoEntity;
import com.reelforge.exception.ResourceNotFoundException;
import com.reelforge.service.VideoService;
import com.reelforge.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {
    
    private final VideoService videoService;
    private static final String UPLOAD_DIR = "uploads/originals";
    
    
    @GetMapping
    public ResponseEntity<?> getAllVideos() {
        log.info("Fetching all uploaded videos");
        
        try {
            List<VideoEntity> videos = videoService.getAllVideos();
            log.info("Retrieved {} videos", videos.size());
            return ResponseEntity.ok(videos);
            
        } catch (Exception e) {
            log.error("Error fetching all videos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching videos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable Long id) {
        log.info("Fetching video by ID: {}", id);
        
        try {
            Optional<VideoEntity> video = videoService.getVideoById(id);
            
            if (video.isEmpty()) {
                log.warn("Video not found with id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Video not found with id: " + id);
            }
            
            log.info("Retrieved video with id: {}", id);
            return ResponseEntity.ok(video.get());
            
        } catch (Exception e) {
            log.error("Error fetching video by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching video: " + e.getMessage());
        }
    }

    
    @PostMapping("/upload")
    public ResponseEntity<VideoUploadResponse> uploadVideo(@RequestParam("video") MultipartFile file) {
        log.info("Starting video upload process for file: {}", file.getOriginalFilename());
        
        // Validate file is not empty
        if (file.isEmpty()) {
            log.warn("Video upload failed: File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(VideoUploadResponse.builder()
                    .success(false)
                    .message("File is empty")
                    .build());
        }
        
        // Validate file is MP4
        String mimeType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String fileExtension = FileUtil.getFileExtension(fileName);
        
        if (!fileExtension.equalsIgnoreCase("mp4")) {
            log.warn("Video upload failed: Invalid file format. Only MP4 files are allowed. File: {}", fileName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(VideoUploadResponse.builder()
                    .success(false)
                    .message("Only MP4 files are allowed")
                    .build());
        }
        
        try {
            // Create upload directory if it doesn't exist
            File uploadDirectory = new File(UPLOAD_DIR);
            if (!uploadDirectory.exists()) {
                if (!uploadDirectory.mkdirs()) {
                    log.error("Failed to create upload directory: {}", UPLOAD_DIR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(VideoUploadResponse.builder()
                            .success(false)
                            .message("Failed to create upload directory")
                            .build());
                }
                log.info("Created upload directory: {}", UPLOAD_DIR);
            }
            
            // Generate unique filename using UUID
            String storedFileName = UUID.randomUUID().toString() + ".mp4";
            String filePath = UPLOAD_DIR + File.separator + storedFileName;
            
            log.debug("Generated unique filename: {} for original file: {}", storedFileName, fileName);
            
            // Save file to disk
            Path uploadPath = Paths.get(filePath);
            Files.write(uploadPath, file.getBytes());
            
            long fileSize = file.getSize();
            log.info("File saved successfully to: {} with size: {} bytes", filePath, fileSize);
            
            // Create and save VideoEntity
            VideoEntity videoEntity = VideoEntity.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileSizeBytes(fileSize)
                .mimeType("video/mp4")
                .uploadedAt(LocalDateTime.now())
                .build();
            
            log.debug("Creating VideoEntity for upload");
            VideoUploadResponse response = videoService.uploadVideo(videoEntity);
            
            log.info("Video uploaded successfully with ID: {}", response.getVideoId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            log.error("Error occurred while saving the file: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoUploadResponse.builder()
                    .success(false)
                    .message("Error occurred while saving the file: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during video upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoUploadResponse.builder()
                    .success(false)
                    .message("Unexpected error during video upload: " + e.getMessage())
                    .build());
        }
    }
    
    @PostMapping("/process")
    public ResponseEntity<?> processVideoWithAudio(@RequestBody VideoProcessRequest request) {
        log.info("Video processing request received for videoId: {} and audioId: {}", 
            request.getVideoId(), request.getAudioId());
        
        try {
            VideoProcessingResponse response = videoService.processVideoWithAudio(request);
            log.info("Video processing endpoint returning success response for videoId: {}", request.getVideoId());
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during video processing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(VideoProcessingResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (RuntimeException e) {
            log.error("Error during video processing for videoId: {}", request.getVideoId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoProcessingResponse.builder()
                    .success(false)
                    .message("Video processing failed: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during video processing for videoId: {}", request.getVideoId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoProcessingResponse.builder()
                    .success(false)
                    .message("Unexpected error during video processing: " + e.getMessage())
                    .build());
        }
    }

    
    @PostMapping("/process/{videoId}")
    public ResponseEntity<?> processVideo(@PathVariable Long videoId) {
        log.info("Video processing request received for videoId: {}", videoId);
        
        try {
            VideoProcessingResponse response = videoService.processVideo(videoId);
            log.info("Video processing endpoint returning success response for videoId: {}", videoId);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Error during video processing for videoId: {}", videoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoProcessingResponse.builder()
                    .success(false)
                    .message("Video processing failed: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during video processing for videoId: {}", videoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoProcessingResponse.builder()
                    .success(false)
                    .message("Unexpected error during video processing: " + e.getMessage())
                    .build());
        }
    }
}
