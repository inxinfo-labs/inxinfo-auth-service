package com.satishlabs.auth.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Fail fast if required JWT config is missing in production.
 */
@Component
public class JwtConfigValidator {

    private final Environment env;
    private final JwtProperties jwt;

    public JwtConfigValidator(Environment env, JwtProperties jwt) {
        this.env = env;
        this.jwt = jwt;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validate() {
        if (!isProd()) return;
        String secret = jwt.getSecret();
        if (secret == null || secret.isBlank() || secret.contains("${")) {
            throw new IllegalStateException(
                    "In production, JWT_SECRET must be set and must not be empty. " +
                    "Set the JWT_SECRET environment variable.");
        }
    }

    private boolean isProd() {
        for (String profile : env.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(profile)) return true;
        }
        return false;
    }
}
