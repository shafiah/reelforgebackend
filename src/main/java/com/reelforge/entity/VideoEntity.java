package com.reelforge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private Long fileSizeBytes;
    
    @Column(nullable = false)
    private String mimeType;
    
    private Integer durationSeconds;
    
    private String resolution;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    private LocalDateTime processedAt;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
