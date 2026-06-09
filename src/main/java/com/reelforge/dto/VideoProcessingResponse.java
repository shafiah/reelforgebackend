package com.reelforge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoProcessingResponse {
    
    private Long videoId;
    
    private String processedVideoPath;
    
    private String message;
    
    private Boolean success;
}
