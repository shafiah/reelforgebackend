package com.reelforge.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoUploadResponse {
    
    private Long videoId;
    
    private String fileName;
    
    private Long fileSizeBytes;
    
    private String mimeType;
    
    private Integer durationSeconds;
    
    private String resolution;
    
    private LocalDateTime uploadedAt;
    
    private String message;
    
    private Boolean success;
}
