package com.reelforge.service.impl;

import com.reelforge.service.FFmpegService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class FFmpegServiceImpl implements FFmpegService {
    
    @Override
    public String replaceAudio(String inputVideoPath, String inputAudioPath, String outputVideoPath) {
        log.info("Starting audio replacement process");
        log.debug("Input video: {}", inputVideoPath);
        log.debug("Input audio: {}", inputAudioPath);
        log.debug("Output video: {}", outputVideoPath);
        
        // Build FFmpeg command
        String[] command = {
            "ffmpeg",
            "-i", inputVideoPath,
            "-i", inputAudioPath,
            "-map", "0:v",
            "-map", "1:a",
            "-shortest",
            outputVideoPath
        };
        
        log.debug("Executing FFmpeg command: {}", String.join(" ", command));
        
        try {
            // Create and start the process
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Merge stderr with stdout
            
            Process process = processBuilder.start();
            log.debug("FFmpeg process started with PID");
            
            // Capture FFmpeg logs
            captureLogs(process);
            
            // Wait for process completion
            int exitCode = process.waitFor();
            log.info("FFmpeg process completed with exit code: {}", exitCode);
            
            // Check exit code
            if (exitCode != 0) {
                log.error("FFmpeg failed with exit code: {}", exitCode);
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode + 
                    " for audio replacement: " + inputAudioPath + " into " + inputVideoPath);
            }
            
            log.info("Audio replacement completed successfully. Output: {}", outputVideoPath);
            return outputVideoPath;
            
        } catch (InterruptedException e) {
            log.error("FFmpeg process was interrupted", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("FFmpeg process was interrupted: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error executing FFmpeg command", e);
            throw new RuntimeException("Error executing FFmpeg command: " + e.getMessage(), e);
        }
    }
    
    /**
     * Captures and logs FFmpeg output in real-time.
     *
     * @param process The FFmpeg process
     */
    private void captureLogs(Process process) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Log FFmpeg output for debugging
                if (line.contains("error") || line.contains("Error") || line.contains("ERROR")) {
                    log.warn("FFmpeg: {}", line);
                } else if (line.contains("frame=") || line.contains("time=")) {
                    // Progress information - log at debug level to avoid noise
                    log.debug("FFmpeg: {}", line);
                } else {
                    log.debug("FFmpeg: {}", line);
                }
            }
        } catch (IOException e) {
            log.warn("Error reading FFmpeg output", e);
        }
    }
}
