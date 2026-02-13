package com.satishlabs.auth.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth.jwt")
@Validated
public class JwtProperties {

    /** Set via JWT_SECRET; in production must be explicitly set (no default). */
    private String secret;

    @Positive
    private long expirationMs = 3600000L;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getExpirationMs() { return expirationMs; }
    public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}
