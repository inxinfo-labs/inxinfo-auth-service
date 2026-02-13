package com.satishlabs.auth.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * In-memory store for 2FA step: after password login, we issue a short-lived temp token.
 * Client sends this token + OTP to verify-2fa to get the full JWT.
 */
@Service
@Slf4j
public class TwoFactorTokenService {

    private static final long TTL_MS = 5 * 60 * 1000; // 5 minutes

    /** Result of consuming a 2FA temp token; visible so AuthServiceImpl can use getAndRemove() return type. */
    public static class Entry {
        public final Long userId;
        public final String email;
        public final String role;
        public final long expiresAt;

        Entry(Long userId, String email, String role, long expiresAt) {
            this.userId = userId;
            this.email = email;
            this.role = role;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    public String createToken(Long userId, String email, String role) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long expiresAt = System.currentTimeMillis() + TTL_MS;
        store.put(token, new Entry(userId, email, role != null ? role : "USER", expiresAt));
        return token;
    }

    public Entry getAndRemove(String token) {
        if (token == null || token.isBlank()) return null;
        Entry e = store.remove(token.trim());
        if (e == null) return null;
        if (e.isExpired()) return null;
        return e;
    }
}
