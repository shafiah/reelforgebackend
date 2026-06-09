package com.reelforge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoProcessRequest {
    
    private Long videoId;
    
    private Long audioId;
}
