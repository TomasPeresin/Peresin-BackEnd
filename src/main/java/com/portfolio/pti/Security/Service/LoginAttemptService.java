package com.portfolio.pti.Security.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    public static final int LOCK_DURATION_MINUTES = 15;
    private static final int MAX_CACHE_SIZE = 1000;

    private static class AttemptInfo {
        int count;
        LocalDateTime lastAttempt;
        LocalDateTime lockedUntil;

        AttemptInfo() {
            this.count = 0;
            this.lastAttempt = LocalDateTime.now();
            this.lockedUntil = null;
        }
    }

    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public String buildClientKey(HttpServletRequest request, String username) {
        String ip = extractClientIp(request);
        String cleanUser = (username != null) ? username.trim().toLowerCase() : "unknown";
        return ip + ":" + cleanUser;
    }

    public String extractClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.trim().isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.trim().isEmpty()) {
            return realIp.trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }

    public boolean isBlocked(String key) {
        cleanupIfNeeded();
        AttemptInfo info = attemptsCache.get(key);
        if (info == null) {
            return false;
        }
        synchronized (info) {
            if (info.lockedUntil != null) {
                if (LocalDateTime.now().isBefore(info.lockedUntil)) {
                    return true;
                } else {
                    // Expiró el tiempo de bloqueo: limpiar registro
                    attemptsCache.remove(key);
                    return false;
                }
            }
            // Si pasaron más de 15 minutos desde el último intento fallido sin llegar al bloqueo, reiniciar contador
            if (info.lastAttempt != null && info.lastAttempt.plusMinutes(LOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
                attemptsCache.remove(key);
                return false;
            }
            return false;
        }
    }

    public long getRemainingLockSeconds(String key) {
        AttemptInfo info = attemptsCache.get(key);
        if (info == null) {
            return 0;
        }
        synchronized (info) {
            if (info.lockedUntil == null) {
                return 0;
            }
            long seconds = Duration.between(LocalDateTime.now(), info.lockedUntil).getSeconds();
            return Math.max(0, seconds);
        }
    }

    public int recordFailedAttempt(String key) {
        cleanupIfNeeded();
        AttemptInfo info = attemptsCache.computeIfAbsent(key, k -> new AttemptInfo());
        synchronized (info) {
            LocalDateTime now = LocalDateTime.now();

            // Si había un bloqueo previo ya vencido o ventana vencida, reiniciar
            if ((info.lockedUntil != null && now.isAfter(info.lockedUntil))
                    || (info.lockedUntil == null && info.lastAttempt.plusMinutes(LOCK_DURATION_MINUTES).isBefore(now))) {
                info.count = 0;
                info.lockedUntil = null;
            }

            info.count++;
            info.lastAttempt = now;

            if (info.count >= MAX_ATTEMPTS) {
                info.lockedUntil = now.plusMinutes(LOCK_DURATION_MINUTES);
                return 0;
            }

            return MAX_ATTEMPTS - info.count;
        }
    }

    public void resetAttempts(String key) {
        if (key != null) {
            attemptsCache.remove(key);
        }
    }

    public String formatRemainingTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (minutes > 0) {
            return minutes + " min " + seconds + " seg";
        }
        return seconds + " seg";
    }

    private void cleanupIfNeeded() {
        if (attemptsCache.size() > MAX_CACHE_SIZE) {
            LocalDateTime now = LocalDateTime.now();
            attemptsCache.entrySet().removeIf(entry -> {
                AttemptInfo info = entry.getValue();
                if (info.lockedUntil != null) {
                    return now.isAfter(info.lockedUntil);
                }
                return info.lastAttempt.plusMinutes(LOCK_DURATION_MINUTES).isBefore(now);
            });
        }
    }
}
