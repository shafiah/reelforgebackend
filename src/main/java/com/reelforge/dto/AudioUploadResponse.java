package com.reelforge.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudioUploadResponse {
    
    private Long audioId;
    
    private String fileName;
    
    private Long fileSizeBytes;
    
    private String mimeType;
    
    private Integer durationSeconds;
    
    private String bitrate;
    
    private LocalDateTime uploadedAt;
    
    private String message;
    
    private Boolean success;
}
