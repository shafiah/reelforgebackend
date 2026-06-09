package com.reelforge.service;

public interface FFmpegService {
    
    /**
     * Replaces the audio track in a video file with a new audio track.
     *
     * @param inputVideoPath Path to the input video file
     * @param inputAudioPath Path to the input audio file
     * @param outputVideoPath Path where the output video will be saved
     * @return The output video path on successful completion
     * @throws RuntimeException if FFmpeg exits with non-zero exit code or process fails
     */
    String replaceAudio(String inputVideoPath, String inputAudioPath, String outputVideoPath);
}
