package com.satishlabs.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Configures DataSource from Render's DATABASE_URL (postgres://user:pass@host:port/dbname).
 * Used when running on Render with a linked PostgreSQL database.
 */
@Configuration
public class RenderDataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "DATABASE_URL", matchIfMissing = false)
    public DataSource dataSource() {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalStateException("DATABASE_URL is set but empty");
        }
        return createDataSourceFromUrl(databaseUrl);
    }

    /**
     * Parse Render/Heroku-style DATABASE_URL (postgres:// or postgresql://) into a Hikari DataSource.
     */
    public static DataSource createDataSourceFromUrl(String url) {
        try {
            if (url.startsWith("postgres://")) {
                url = "postgresql://" + url.substring("postgres://".length());
            }
            URI uri = new URI(url);
            String[] userInfo = uri.getUserInfo() != null ? uri.getUserInfo().split(":", 2) : new String[]{"", ""};
            String username = userInfo.length > 0 ? userInfo[0] : "";
            String password = userInfo.length > 1 ? userInfo[1] : "";
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            if (path != null && path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path != null && path.contains("?")) {
                path = path.substring(0, path.indexOf('?'));
            }
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + (path != null ? path : "postgres");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            return new HikariDataSource(config);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse DATABASE_URL for Render/PostgreSQL", e);
        }
    }
}
