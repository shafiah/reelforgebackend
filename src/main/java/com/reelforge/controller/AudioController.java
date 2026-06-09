package com.reelforge.controller;

import com.reelforge.dto.AudioUploadResponse;
import com.reelforge.entity.AudioEntity;
import com.reelforge.service.AudioService;
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
@RequestMapping("/api/audios")
@RequiredArgsConstructor
@Slf4j
public class AudioController {
    
    private final AudioService audioService;
    private static final String UPLOAD_DIR = "uploads/audios";
    
    @GetMapping
    public ResponseEntity<?> getAllAudios() {
        log.info("Fetching all uploaded audios");
        
        try {
            List<AudioEntity> audios = audioService.getAllAudio();
            log.info("Retrieved {} audios", audios.size());
            return ResponseEntity.ok(audios);
            
        } catch (Exception e) {
            log.error("Error fetching all audios", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching audios: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAudioById(@PathVariable Long id) {
        log.info("Fetching audio by ID: {}", id);
        
        try {
            Optional<AudioEntity> audio = audioService.getAudioById(id);
            
            if (audio.isEmpty()) {
                log.warn("Audio not found with id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Audio not found with id: " + id);
            }
            
            log.info("Retrieved audio with id: {}", id);
            return ResponseEntity.ok(audio.get());
            
        } catch (Exception e) {
            log.error("Error fetching audio by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching audio: " + e.getMessage());
        }
    }

    
    @PostMapping("/upload")
    public ResponseEntity<AudioUploadResponse> uploadAudio(@RequestParam("audio") MultipartFile audioFile) {
        log.info("Starting audio upload process for file: {}", audioFile.getOriginalFilename());
        
        // Validate file is not empty
        if (audioFile.isEmpty()) {
            log.warn("Audio upload failed: File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AudioUploadResponse.builder()
                    .success(false)
                    .message("File is empty")
                    .build());
        }
        
        // Validate file is MP3
        String fileName = audioFile.getOriginalFilename();
        String fileExtension = FileUtil.getFileExtension(fileName);
        
        if (!fileExtension.equalsIgnoreCase("mp3")) {
            log.warn("Audio upload failed: Invalid file format. Only MP3 files are allowed. File: {}", fileName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AudioUploadResponse.builder()
                    .success(false)
                    .message("Only MP3 files are allowed")
                    .build());
        }
        
        try {
            // Create upload directory if it doesn't exist
            File uploadDirectory = new File(UPLOAD_DIR);
            if (!uploadDirectory.exists()) {
                if (!uploadDirectory.mkdirs()) {
                    log.error("Failed to create upload directory: {}", UPLOAD_DIR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(AudioUploadResponse.builder()
                            .success(false)
                            .message("Failed to create upload directory")
                            .build());
                }
                log.info("Created upload directory: {}", UPLOAD_DIR);
            }
            
            // Generate unique filename using UUID
            String storedFileName = UUID.randomUUID().toString() + ".mp3";
            String filePath = UPLOAD_DIR + File.separator + storedFileName;
            
            log.debug("Generated unique filename: {} for original file: {}", storedFileName, fileName);
            
            // Save file to disk
            Path uploadPath = Paths.get(filePath);
            Files.write(uploadPath, audioFile.getBytes());
            
            long fileSize = audioFile.getSize();
            log.info("File saved successfully to: {} with size: {} bytes", filePath, fileSize);
            
            // Create and save AudioEntity
            AudioEntity audioEntity = AudioEntity.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileSizeBytes(fileSize)
                .mimeType("audio/mpeg")
                .uploadedAt(LocalDateTime.now())
                .build();
            
            log.debug("Creating AudioEntity for upload");
            AudioEntity savedAudio = audioService.uploadAudio(audioEntity);
            
            log.info("Audio uploaded successfully with ID: {}", savedAudio.getId());
            
            // Build and return response
            AudioUploadResponse response = AudioUploadResponse.builder()
                .audioId(savedAudio.getId())
                .fileName(savedAudio.getFileName())
                .fileSizeBytes(savedAudio.getFileSizeBytes())
                .mimeType(savedAudio.getMimeType())
                .durationSeconds(savedAudio.getDurationSeconds())
                .bitrate(savedAudio.getBitrate())
                .uploadedAt(savedAudio.getUploadedAt())
                .success(true)
                .message("Audio uploaded successfully")
                .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            log.error("Error occurred while saving the file: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AudioUploadResponse.builder()
                    .success(false)
                    .message("Error occurred while saving the file: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during audio upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AudioUploadResponse.builder()
                    .success(false)
                    .message("Unexpected error during audio upload: " + e.getMessage())
                    .build());
        }
    }
}
