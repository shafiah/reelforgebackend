package com.reelforge.service;

import com.reelforge.dto.VideoProcessRequest;
import org.springframework.core.io.Resource;
import com.reelforge.dto.VideoProcessingResponse;
import com.reelforge.dto.VideoUploadResponse;
import com.reelforge.entity.VideoEntity;

import java.util.List;
import java.util.Optional;

public interface VideoService {
    
    VideoUploadResponse uploadVideo(VideoEntity video);
    
    Optional<VideoEntity> getVideoById(Long id);
    
    List<VideoEntity> getAllVideos();
    
    VideoEntity updateVideo(Long id, VideoEntity video);
    
    void deleteVideo(Long id);
    
    List<VideoEntity> getVideosByResolution(String resolution);
    
    VideoProcessingResponse processVideo(Long videoId);
    
    VideoProcessingResponse processVideoWithAudio(VideoProcessRequest request);
    
    Resource downloadVideo(Long videoId);
}
