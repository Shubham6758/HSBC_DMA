package com.coforge.hsbcdma.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // token -> expiryTimeEpochSeconds
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, long expiryEpochSeconds) {
        blacklist.put(token, expiryEpochSeconds);
    }

    public boolean isBlacklisted(String token) {
        Long exp = blacklist.get(token);
        if (exp == null) return false;

        // remove if already expired
        if (Instant.now().getEpochSecond() > exp) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}

