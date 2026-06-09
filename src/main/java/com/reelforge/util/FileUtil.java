package com.reelforge.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtil {
    
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm"
    );
    
    private static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList(
        "audio/mpeg", "audio/wav", "audio/ogg", "audio/flac", "audio/m4a"
    );
    
    public static boolean isValidVideoMimeType(String mimeType) {
        return ALLOWED_VIDEO_TYPES.contains(mimeType);
    }
    
    public static boolean isValidAudioMimeType(String mimeType) {
        return ALLOWED_AUDIO_TYPES.contains(mimeType);
    }
    
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : 0;
    }
    
    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
}
