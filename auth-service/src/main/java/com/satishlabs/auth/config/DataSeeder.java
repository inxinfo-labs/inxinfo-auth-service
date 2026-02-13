package com.satishlabs.auth.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.util.AuthProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds default admin user on startup if not present and app.seed.enabled is true.
 * Email and password from app.seed.* (SEED_ADMIN_EMAIL, SEED_ADMIN_PASSWORD).
 */
@Component
@Order(1)
@Slf4j
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Seed seed = appProperties.getSeed();
        if (!seed.isEnabled()) return;
        String adminEmail = seed.getAdminEmail();
        if (adminEmail == null || adminEmail.isBlank()) return;
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(seed.getAdminDefaultPassword()))
                    .firstName("Admin")
                    .lastName("User")
                    .name("Admin User")
                    .role(Role.ADMIN)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: {} (change password after first login)", adminEmail);
        }
    }
}
