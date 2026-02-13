package com.satishlabs.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Centralized application configuration. No hardcoded URLs, paths, or flags in code.
 */
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    private final Upload upload = new Upload();
    private final Mail mail = new Mail();
    private final Frontend frontend = new Frontend();
    private final Cors cors = new Cors();
    private final Seed seed = new Seed();

    public Upload getUpload() { return upload; }
    public Mail getMail() { return mail; }
    public Frontend getFrontend() { return frontend; }
    public Cors getCors() { return cors; }
    public Seed getSeed() { return seed; }

    public static class Upload {
        @NotBlank(message = "app.upload.profile-pic-path must be set")
        private String profilePicPath = "uploads/profile-pics";

        public String getProfilePicPath() { return profilePicPath; }
        public void setProfilePicPath(String profilePicPath) { this.profilePicPath = profilePicPath; }
    }

    public static class Mail {
        private boolean enabled = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class Frontend {
        @NotBlank(message = "app.frontend.url must be set in production")
        private String url = "http://localhost:3000";

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class Cors {
        /** Comma-separated origins; parsed to list when retrieved. Set via CORS_ALLOWED_ORIGINS. */
        @NotNull(message = "app.cors.allowed-origins must be set")
        private String allowedOrigins = "http://localhost:3000,http://127.0.0.1:3000";
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
        private List<String> allowedHeaders = List.of("*");
        private boolean allowCredentials = true;

        /** Returns allowed origins as list (splits comma-separated string). */
        public List<String> getAllowedOriginsList() {
            if (allowedOrigins == null || allowedOrigins.isBlank()) return List.of();
            return java.util.Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        }
        public String getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(String allowedOrigins) { this.allowedOrigins = allowedOrigins; }
        public List<String> getAllowedMethods() { return allowedMethods; }
        public void setAllowedMethods(List<String> allowedMethods) { this.allowedMethods = allowedMethods; }
        public List<String> getAllowedHeaders() { return allowedHeaders; }
        public void setAllowedHeaders(List<String> allowedHeaders) { this.allowedHeaders = allowedHeaders; }
        public boolean isAllowCredentials() { return allowCredentials; }
        public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }
    }

    public static class Seed {
        private boolean enabled = true;
        private String adminEmail = "admin@inxinfo.com";
        private String adminDefaultPassword = "Admin@123";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAdminEmail() { return adminEmail; }
        public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
        public String getAdminDefaultPassword() { return adminDefaultPassword; }
        public void setAdminDefaultPassword(String adminDefaultPassword) { this.adminDefaultPassword = adminDefaultPassword; }
    }
}
