package com.satishlabs.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Logs loaded configuration at startup (excluding secrets) for debugging and validation.
 */
@Component
public class ConfigLogger {

    private static final Logger log = LoggerFactory.getLogger(ConfigLogger.class);

    private final Environment env;
    private final AppProperties app;
    private final JwtProperties jwt;

    public ConfigLogger(Environment env, AppProperties app, JwtProperties jwt) {
        this.env = env;
        this.app = app;
        this.jwt = jwt;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logConfig() {
        String profile = String.join(",", env.getActiveProfiles().length > 0 ? env.getActiveProfiles() : new String[]{"default"});
        log.info("Configuration loaded: activeProfiles={}, server.port={}, app.frontend.url={}, app.upload.profilePicPath={}, app.cors.allowedOrigins={}, auth.jwt.expirationMs={}, auth.jwt.secretSet={}",
                profile,
                env.getProperty("server.port"),
                app.getFrontend().getUrl(),
                app.getUpload().getProfilePicPath(),
                app.getCors().getAllowedOriginsList(),
                jwt.getExpirationMs(),
                jwt.getSecret() != null && !jwt.getSecret().isBlank());
    }
}
