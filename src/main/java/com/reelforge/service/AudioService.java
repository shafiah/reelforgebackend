package com.reelforge.service;

import com.reelforge.entity.AudioEntity;

import java.util.List;
import java.util.Optional;

public interface AudioService {
    
    AudioEntity uploadAudio(AudioEntity audio);
    
    Optional<AudioEntity> getAudioById(Long id);
    
    List<AudioEntity> getAllAudio();
    
    AudioEntity updateAudio(Long id, AudioEntity audio);
    
    void deleteAudio(Long id);
    
    List<AudioEntity> getAudioByBitrate(String bitrate);
}
