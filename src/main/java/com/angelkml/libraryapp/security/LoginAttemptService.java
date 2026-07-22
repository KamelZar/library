package com.angelkml.libraryapp.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptService {

    private final int maxAttempts;
    private final Duration blacklistDuration;

    private final ConcurrentHashMap<String, Integer> failureCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> blacklistedUntil = new ConcurrentHashMap<>();

    LoginAttemptService(@Value("${app.security.max-login-attempts}") int maxAttempts,
                         @Value("${app.security.blacklist-duration-minutes}") long blacklistDurationMinutes) {
        this.maxAttempts = maxAttempts;
        this.blacklistDuration = Duration.ofMinutes(blacklistDurationMinutes);
    }

    public void loginFailed(String ip) {
        int count = failureCounts.merge(ip, 1, Integer::sum);
        if (count >= maxAttempts) {
            blacklistedUntil.put(ip, Instant.now().plus(blacklistDuration));
        }
    }

    public void loginSucceeded(String ip) {
        failureCounts.remove(ip);
        blacklistedUntil.remove(ip);
    }

    public boolean isBlacklisted(String ip) {
        Instant expiry = blacklistedUntil.get(ip);
        if (expiry == null) {
            return false;
        }
        if (Instant.now().isAfter(expiry)) {
            blacklistedUntil.remove(ip);
            failureCounts.remove(ip);
            return false;
        }
        return true;
    }
}
