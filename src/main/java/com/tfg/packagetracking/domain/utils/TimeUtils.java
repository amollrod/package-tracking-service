package com.tfg.packagetracking.domain.utils;

import java.time.Instant;

public class TimeUtils {
    /**
     * Obtains the current timestamp in seconds (UNIX epoch format).
     */
    public static long getCurrentTimestamp() {
        return Instant.now().getEpochSecond();
    }
}