package com.reelforge.service.impl;

import com.reelforge.entity.AudioEntity;
import com.reelforge.exception.ResourceNotFoundException;
import com.reelforge.repository.AudioRepository;
import com.reelforge.service.AudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AudioServiceImpl implements AudioService {
    
    private final AudioRepository audioRepository;
    
    @Override
    public AudioEntity uploadAudio(AudioEntity audio) {
        return audioRepository.save(audio);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<AudioEntity> getAudioById(Long id) {
        return audioRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AudioEntity> getAllAudio() {
        return audioRepository.findAll();
    }
    
    @Override
    public AudioEntity updateAudio(Long id, AudioEntity audio) {
        AudioEntity existingAudio = audioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Audio not found with id: " + id));
        
        if (audio.getFileName() != null) {
            existingAudio.setFileName(audio.getFileName());
        }
        if (audio.getBitrate() != null) {
            existingAudio.setBitrate(audio.getBitrate());
        }
        if (audio.getDurationSeconds() != null) {
            existingAudio.setDurationSeconds(audio.getDurationSeconds());
        }
        
        return audioRepository.save(existingAudio);
    }
    
    @Override
    public void deleteAudio(Long id) {
        if (!audioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Audio not found with id: " + id);
        }
        audioRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AudioEntity> getAudioByBitrate(String bitrate) {
        return audioRepository.findByBitrate(bitrate);
    }
}
