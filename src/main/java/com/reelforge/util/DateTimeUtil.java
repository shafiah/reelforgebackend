package com.reelforge.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }
    
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }
    
    public static long getSecondsDifference(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return java.time.temporal.ChronoUnit.SECONDS.between(start, end);
        }
        return 0;
    }
}
